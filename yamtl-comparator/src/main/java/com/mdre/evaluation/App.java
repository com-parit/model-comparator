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
import com.mdre.evaluation.EmfaticToEcoreModelService;
import com.mdre.evaluation.Uml2Ecore;

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

    @PostMapping("/ecore2emfatic")
    public String getEmfaticFromEcore(@RequestParam("ecoreModel") MultipartFile ecoreModel) {
        try {
			EcoreToEmfaticService generator = new EcoreToEmfaticService();
            String modelFilePath = saveFile(ecoreModel, ".ecore");
            System.out.println(rootProjectPath);
            System.out.println(modelFilePath);
            generator.run(modelFilePath, rootProjectPath);
            Path path = Paths.get(modelFilePath.substring(0, modelFilePath.length() - ".ecore".length()) + ".emf");
            String response = Files.readString(path);
            Files.delete(path);
            Files.delete(Paths.get(modelFilePath));
            return response;
        } catch (Exception e) {
            return "Could not generate emfatic file " + e;
        }
    }

    @PostMapping("/emfatic2ecore")
    public String getEcoreFromEmfatic(@RequestParam("emfaticModel") MultipartFile emfaticModel) {
        try {
            EmfaticToEcoreModelService emf2ecore = new EmfaticToEcoreModelService();
            String emfaticFilePath = saveFile(emfaticModel, "");
            System.out.println(rootProjectPath);
            System.out.println(emfaticFilePath);
            emf2ecore.run(emfaticFilePath);
            Path path = Paths.get(emfaticFilePath.substring(0, emfaticFilePath.length() - ".emf".length()) + ".ecore");
            String response = Files.readString(path);
            Files.delete(path);
            Files.delete(Paths.get(emfaticFilePath));
            return response;
        } catch (Exception e) {
            return "Could not generate ecore file " + e;
        }
    }

   @PostMapping("/uml2ecore")
    public String getEcoreFromUml(@RequestParam("umlModel") MultipartFile umlModel) {
        try {
            Uml2Ecore uml2ecore = new Uml2Ecore();
            String umlFilePath = saveFile(umlModel, "");
            System.out.println(rootProjectPath);
            System.out.println(umlFilePath);
            String ecoreFilePath = umlFilePath.substring(0, umlFilePath.length() - ".uml".length()) + ".ecore";
            System.out.println(ecoreFilePath);
			Uml2Ecore.convertUmlToEcore(
				umlFilePath, 
                ecoreFilePath
            );
            Path path = Paths.get(ecoreFilePath);
            String response = Files.readString(path);
            System.out.print(response);
            Files.delete(path);
            Files.delete(Paths.get(umlFilePath));
            return response;
        } catch (Exception e) {
            return "Could not generate uml file " + e;
        }
    }

    @PostMapping("/compare")
    public Object uploadFiles(
        @RequestParam("groundTruthModel") MultipartFile groundTruthModel, 
        @RequestParam("predictedModel") MultipartFile predictedModel,
        @RequestParam("projectName") String projectName
        ) {
        try {
            String originalModelFilePath = saveFile(groundTruthModel, ".ecore");
            String predictedModelFilePath = saveFile(predictedModel, ".ecore");
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

    private String saveFile(MultipartFile file, String extension) throws IOException {
        String uploadDir = rootProjectPath;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = uploadDir + file.getOriginalFilename() + extension;
        File dest = new File(filePath);
        file.transferTo(dest);
        return filePath;
    }
}
