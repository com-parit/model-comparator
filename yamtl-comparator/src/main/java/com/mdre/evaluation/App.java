package com.mdre.evaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

import com.mdre.evaluation.EvaluateClasses;
import com.mdre.evaluation.EcoreToEmfaticService;

@SpringBootApplication
@RestController
public class App {
    String rootProjectPath = "/media/jawad/secondaryStorage/projects/mdre/top10/model-comparator/yamtl-comparator/";

    public static void main(String[] args) {
      SpringApplication.run(App.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s!", name);
    }

    @PostMapping("/emfatic")
    public String getEmfatic(@RequestParam("ecoreModel") MultipartFile ecoreModel) {
        try {
			EcoreToEmfaticService generator = new EcoreToEmfaticService();
            String modelFilePath = saveEcoreFile(ecoreModel);
            System.out.println(rootProjectPath);
            System.out.println(modelFilePath);
            generator.run(modelFilePath, rootProjectPath);
            Path path = Paths.get(modelFilePath.substring(0, modelFilePath.length() - ".ecore".length()) + ".emf");
            String response = Files.readString(path);
            Files.delete(path);
            return response;
        } catch (Exception e) {
            return "Could not generate emfatic file " + e;
        }
    }

    @PostMapping("/compare")
    public Object uploadFiles(
        @RequestParam("groundTruthModel") MultipartFile groundTruthModel, 
        @RequestParam("predictedModel") MultipartFile predictedModel,
        @RequestParam("projectName") String projectName
        ) {
        try {
            String originalModelFilePath = saveEcoreFile(groundTruthModel);
            String predictedModelFilePath = saveEcoreFile(predictedModel);
    		EvaluateClasses evaluationEngine = new EvaluateClasses();
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

    private String saveEcoreFile(MultipartFile file) throws IOException {
        String uploadDir = rootProjectPath;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = uploadDir + file.getOriginalFilename() + ".ecore";
        File dest = new File(filePath);
        file.transferTo(dest);
        return filePath;
    }
}
