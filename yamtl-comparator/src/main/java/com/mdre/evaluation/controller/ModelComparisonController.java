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
import com.mdre.evaluation.Utils.FileUtils;

@RestController
public class ModelComparisonController {
    @PostMapping("/compare")
    public Object compareUsingDigests(
        @RequestParam("groundTruthModel") MultipartFile groundTruthModel, 
        @RequestParam("predictedModel") MultipartFile predictedModel,
        @RequestParam("config") MultipartFile config,
        @RequestParam("projectName") String projectName
        ) {
        try {
            String originalModelFilePath = FileUtils.saveFile(groundTruthModel, ".ecore");
            String predictedModelFilePath = FileUtils.saveFile(predictedModel, ".ecore");
            String configFilePath = FileUtils.saveFile(config, "");
            JSONObject configuration = new JSONObject();
            try (FileReader reader = new FileReader(configFilePath)) {
                JSONTokener tokener = new JSONTokener(reader);
                configuration = new JSONObject(tokener);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(configuration);
       		ModelComparisonService evaluationEngine = new ModelComparisonService(configuration);
            Boolean includeDependencies = true;
            ArrayList<HashMap<String, String>> models = new ArrayList<HashMap<String, String>>();
            models.add(new HashMap<String, String>(){{
				put("original", originalModelFilePath);
				put("generated", predictedModelFilePath);
                put("projectName", projectName);
			}});
            HashMap<String,JSONObject> results = evaluationEngine.compareModels(models, includeDependencies);
            File originalModelFile = new File(originalModelFilePath);
            File predictedModelFile = new File(predictedModelFilePath);
            originalModelFile.delete();
            predictedModelFile.delete();
            return new JSONObject(results).toString();
        } catch (IOException e) {
            return "Internal Server Error: " + e.getMessage();
        }
    }
}