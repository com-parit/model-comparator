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

import com.mdre.evaluation.services.EmfaticToEcoreService;
import com.mdre.evaluation.utils.FileUtils;

@RestController
public class EmfaticToEcoreController {
    String rootProjectPath = "/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/";
    @PostMapping("/emfatic2ecore")
    public String getEcoreFromEmfatic(@RequestParam("emfaticModel") MultipartFile emfaticModel) {
        String emfaticFilePath = "";
        try {
            EmfaticToEcoreService emf2ecore = new EmfaticToEcoreService();
            emfaticFilePath = FileUtils.saveFile(emfaticModel, "");
            System.out.println(rootProjectPath);
            System.out.println(emfaticFilePath);
            emf2ecore.run(emfaticFilePath);
            Path path = Paths.get(emfaticFilePath.substring(0, emfaticFilePath.length() - ".emf".length()) + ".ecore");
            String response = Files.readString(path);
            System.out.println(response);
            Files.delete(path);
            Files.delete(Paths.get(emfaticFilePath));
            System.out.println("Generated ecore model");
            return response;
        } catch (Exception e) {
            try {
                Files.delete(Paths.get(emfaticFilePath));
            } catch (Exception fe) {
                System.out.println(e);
                System.out.println(fe);
            }
            return "Could not generate ecore file " + e;
        }
    }
}