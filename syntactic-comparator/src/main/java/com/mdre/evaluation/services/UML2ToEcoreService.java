package com.mdre.evaluation.services;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.io.File;


public class UML2ToEcoreService {

    public static String convertToEcore(String umlModelPath) {
        String ecoreFilePath = umlModelPath.substring(0, umlModelPath.length() - ".uml".length()) + ".ecore";
        // Initialize the UML2 package (Model)
        UMLPackage.eINSTANCE.eClass();

        // Create a resource set
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, new XMIResourceFactoryImpl());

        // Load the UML model
        Resource umlResource = resourceSet.getResource(URI.createFileURI(umlModelPath), true);

        // Assuming the UML model is the first element in the resource
        Model umlModel = (Model) umlResource.getContents().get(0);

        // Convert the UML model to Ecore
        Map<String, String> options = new HashMap<>();
        Collection<EPackage> ecorePackages = UMLUtil.convertToEcore(umlModel, options);

        // Save each EPackage to an Ecore file
        for (EPackage ePackage : ecorePackages) {
            // Register the XMI resource factory for the .ecore extension
            Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
            reg.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

            // Create a resource set and resource
            ResourceSet resourceSet1 = new ResourceSetImpl();
            URI fileURI = URI.createFileURI(ecoreFilePath);
            Resource resource1 = resourceSet.createResource(fileURI);
            resourceSet1.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, new XMIResourceFactoryImpl());

            // Add the EPackage to the resource
            resource1.getContents().add(ePackage);

            // Save the resource
            try {
                resource1.save(Collections.EMPTY_MAP);
                System.out.println("Ecore model saved successfully.");
                return ecoreFilePath;
            } catch (IOException e) {
                e.printStackTrace();
                return ecoreFilePath;
            }
        }
        return ecoreFilePath;
    }
    public static void main(String[] args) {
        // UML2ToEcoreService.convertToEcore("/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/src/main/resources/sample_model.uml");
        // Specify the directory path
        // File directory = new File("/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/functional-tests/uml_models/data");
        // Integer failed = 0;
        // Integer converted = 0;

        // // Check if the directory exists and is a directory
        // if (directory.exists() && directory.isDirectory()) {
        //     // Get all files in the directory
        //     File[] files = directory.listFiles();
        //     if (files != null) {
        //         for (File file : files) {
        //             if (file.isFile() && file.getName().endsWith(".xmi")) {
        //                 // Create a new file name with the ".uml" extension
        //                 String newFileName = file.getName().replace(".xmi", ".uml");
        //                 File newFile = new File(directory, newFileName);

        //                 // Rename the file
        //                 boolean success = file.renameTo(newFile);
        //                 if (success) {
        //                     System.out.println("Renamed: " + file.getAbsolutePath() + " -> " + newFile.getAbsolutePath());
        //                 } else {
        //                     System.out.println("Failed to rename: " + file.getAbsolutePath());
        //                 }
        //                 file = newFile;
        //             }
        //             String fileName = file.getAbsolutePath();
        //             System.out.println(fileName);
        //             try {
        //                 UML2ToEcoreService.convertToEcore(fileName);
        //                 converted += 1;
        //             } catch(Exception e) {
        //                 System.out.println("could not convert");
        //                 failed += 1;
        //             }
        //         }
        //     } else {
        //         System.out.println("No files found in the directory.");
        //     }
        // } else {
        //     System.out.println("The specified path is not a directory or does not exist.");
        // }
        // System.out.println(converted);
        // System.out.println(failed);
    }
}
