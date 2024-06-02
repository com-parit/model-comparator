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

import com.mdre.evaluation.EvaluateClasses;

@SpringBootApplication
@RestController
public class App {

    public static void main(String[] args) {
      SpringApplication.run(App.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s!", name);
    }

    @PostMapping("/upload")
    public String uploadFiles(
        @RequestParam("groundTruthModel") MultipartFile groundTruthModel, 
        @RequestParam("predictedModel") MultipartFile predictedModel,
        @RequestParam("projectName") String projectName
        ) {
        try {
            String originalModelFilePath = saveFile(groundTruthModel);
            String predictedModelFilePath = saveFile(predictedModel);
    		EvaluateClasses evaluationEngine = new EvaluateClasses();
            Boolean includeDependencies = true;
            ArrayList<HashMap<String, String>> models = new ArrayList<HashMap<String, String>>();
            models.add(new HashMap<String, String>(){{
				put("original", originalModelFilePath);
				put("generated", predictedModelFilePath);
                put("projectName", projectName);
			}});
            HashMap<String,JSONObject> results = evaluationEngine.compareModels(models, includeDependencies);
            return results.toString();
        } catch (IOException e) {
            return "Failed to save files: " + e.getMessage();
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "/media/jawad/secondaryStorage/projects/mdre/top10/demo/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = uploadDir + file.getOriginalFilename();
        File dest = new File(filePath);
        file.transferTo(dest);
        return filePath;
    }
}
