package com.mdre.evaluation.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.json.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mdre.evaluation.services.modelComparisonService.ModelComparisonService;
import com.mdre.evaluation.utils.FileUtils;
import com.mdre.evaluation.utils.JSONtoDTOMapper;
import com.mdre.evaluation.services.JsonSchemaValidatorService;
import com.mdre.evaluation.config.Constants;
import com.mdre.evaluation.dtos.ModelComparisonConfigurationDTO;

@RestController
public class ModelComparisonController {
    private JsonSchemaValidatorService jsonSchemaValidatorService = new JsonSchemaValidatorService();

    @PostMapping("/compare")
    public Object compareUsingDigests(
        @RequestParam("groundTruthModel") MultipartFile groundTruthModel, 
        @RequestParam("predictedModel") MultipartFile predictedModel,
        @RequestParam("config") MultipartFile config
        ) {
        String originalModelFilePath = "";
        String predictedModelFilePath = "";
        String configFilePath = "";
        try {
            originalModelFilePath = FileUtils.saveFile(groundTruthModel, ".ecore");
            predictedModelFilePath = FileUtils.saveFile(predictedModel, ".ecore");
            configFilePath = FileUtils.saveFile(config, "");
            JSONObject configuration = new JSONObject();
            try (FileReader reader = new FileReader(configFilePath)) {
                JSONTokener tokener = new JSONTokener(reader);
                configuration = new JSONObject(tokener);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // validate schema for configuration
            try {
                jsonSchemaValidatorService.validate(configuration.toString(), Constants.CONFIGURATION_SCHEMA_KEY);
            } catch (Exception e) {
                System.out.println("Incorrect config in request");
                return new JSONObject("{\"response\":\"The configuration file does not conform to the schema\"}").toString();
            }
            ModelComparisonConfigurationDTO modelComparisonConfigurationDTO = JSONtoDTOMapper.mapToModelComparisonConfigurationDTO(configuration);
       		ModelComparisonService evaluationEngine = new ModelComparisonService(modelComparisonConfigurationDTO);
            HashMap<String,JSONObject> results = evaluationEngine.compareModels(originalModelFilePath, predictedModelFilePath);
            File originalModelFile = new File(originalModelFilePath);
            File predictedModelFile = new File(predictedModelFilePath);
            File configFile = new File(configFilePath);
            originalModelFile.delete();
            predictedModelFile.delete();
            configFile.delete();
            return new JSONObject(results).toString();
        } catch (IOException e) {
            try {
                File originalModelFile = new File(originalModelFilePath);
                File predictedModelFile = new File(predictedModelFilePath);
                File configFile = new File(configFilePath);
                originalModelFile.delete();
                predictedModelFile.delete();
                configFile.delete();
            } catch (Exception fe) {
                System.out.println(e);
                System.out.println(fe);
            }
            return "Internal Server Error: " + e.getMessage();
        }
    }
}