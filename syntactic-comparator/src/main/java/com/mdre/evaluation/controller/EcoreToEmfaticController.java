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

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mdre.evaluation.services.EcoreToEmfaticService;
import com.mdre.evaluation.utils.FileUtils;

@RestController
public class EcoreToEmfaticController {
    String rootProjectPath = "/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/";
    @PostMapping("/ecore2emfatic")
    public String getEmfaticFromEcore(@RequestParam("ecoreModel") MultipartFile ecoreModel) {
        String modelFilePath = "";
        try {
			EcoreToEmfaticService generator = new EcoreToEmfaticService();
            modelFilePath = FileUtils.saveFile(ecoreModel, ".ecore");
            System.out.println(rootProjectPath);
            System.out.println(modelFilePath);
            generator.run(modelFilePath, rootProjectPath);
            Path path = Paths.get(modelFilePath.substring(0, modelFilePath.length() - ".ecore".length()) + ".emf");
            String response = Files.readString(path);
            Files.delete(path);
            Files.delete(Paths.get(modelFilePath));
            return response;
        } catch (Exception e) {
            try {
                Files.delete(Paths.get(modelFilePath));
            } catch (Exception fe) {
                System.out.println(e);
                System.out.println(fe);
            }
            return "Could not generate emfatic file " + e;
        }
    }
}