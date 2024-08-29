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

import com.mdre.evaluation.services.UML2ToEcoreService;
import com.mdre.evaluation.utils.FileUtils;

@RestController
public class UML2ToEcoreController {
    String rootProjectPath = "/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/";
    @PostMapping("/uml2Toecore")
    public String getEcoreFromUML2(@RequestParam("uml2Model") MultipartFile umlModel) {
        String umlFilePath = "";
        try {
            UML2ToEcoreService uml2ecore = new UML2ToEcoreService();
            umlFilePath = FileUtils.saveFile(umlModel, "");
            System.out.println(rootProjectPath);
            System.out.println(umlFilePath);
            String ecoreFilePath = uml2ecore.convertToEcore(umlFilePath);
            Path path = Paths.get(ecoreFilePath);
            String response = Files.readString(path);
            Files.delete(path);
            Files.delete(Paths.get(umlFilePath));
            System.out.println("Generated ecore model");
            return response;
        } catch (Exception e) {
            try {
                Files.delete(Paths.get(umlFilePath));
            } catch (Exception fe) {
                System.out.println(e);
                System.out.println(fe);
            }
            return "Could not generate ecore file " + e;
        }
    }
}