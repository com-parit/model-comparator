package com.mdre.evaluation;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.eclipse.uml2.uml.util.UMLUtil.UML2EcoreConverter;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.resource.UMLResource;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Uml2Ecore {
    public static void saveEcoreToFile(Collection<EPackage> ecorePackages, String outputPath) {
        // Create a resource set and a resource factory registry
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

        // Create a URI for the output file
        URI fileURI = URI.createFileURI(outputPath);

        // Create a resource for this URI
        Resource ecoreResource = resourceSet.createResource(fileURI);

        // Add the EPackages to the resource
        ecoreResource.getContents().addAll(ecorePackages);

        // Save the resource
        try {
            ecoreResource.save(null);
            System.out.println("Ecore model saved to " + fileURI.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void convertUmlToEcore(String umlFilePath, String ecoreFilePath) {
		try {
	        // Initialize the model
	        UMLPackage.eINSTANCE.eClass();
	        
	        // Register the XMI resource factory for the .uml extension
	        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml", new XMIResourceFactoryImpl());
	        
	        // Create a resource set to hold the resources.
	        ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
			
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
					.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
	        
	        // Get the URI of the model file
	        URI fileURI = URI.createFileURI(umlFilePath);
	        
	        // Demand load the resource for this file
	        Resource resource = resourceSet.getResource(fileURI, true);
	        
	        // Assuming the first element in the contents is the UML model
	        if (!resource.getContents().isEmpty() && resource.getContents().get(0) instanceof Package) {
	            Map<String, String> options = new HashMap<>();
	    		options.put(UML2EcoreConverter.OPTION__ECORE_TAGGED_VALUES, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__REDEFINING_OPERATIONS, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__REDEFINING_PROPERTIES, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__SUBSETTING_PROPERTIES, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__UNION_PROPERTIES, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__DERIVED_FEATURES, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__DUPLICATE_OPERATIONS, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__DUPLICATE_OPERATION_INHERITANCE, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__DUPLICATE_FEATURES, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__DUPLICATE_FEATURE_INHERITANCE, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__SUPER_CLASS_ORDER, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__ANNOTATION_DETAILS, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__INVARIANT_CONSTRAINTS, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__OPERATION_BODIES, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__COMMENTS, UMLUtil.OPTION__PROCESS);

	    		options.put(UML2EcoreConverter.OPTION__CAMEL_CASE_NAMES, UMLUtil.OPTION__IGNORE);	        	
				Collection<EPackage> ecore = UMLUtil.convertToEcore((Package) resource.getContents().get(0), options);
	        	Uml2Ecore.saveEcoreToFile(ecore, ecoreFilePath);
				 
	        } else {
	            throw new RuntimeException("Failed to load the UML model from the file: ");
	        }
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
