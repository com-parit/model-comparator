package com.mdre.evaluation.utils;

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

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    public static String saveFile(MultipartFile file, String extension) throws IOException {
    	String rootProjectPath = "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/yamtl-comparator/";
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