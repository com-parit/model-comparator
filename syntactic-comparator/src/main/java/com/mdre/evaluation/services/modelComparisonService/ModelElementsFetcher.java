package com.mdre.evaluation.services.modelComparisonService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.io.FileWriter;
import java.io.IOException;
import org.json.*;
import java.io.FileNotFoundException;
import java.io.File;

import subtyping.EMFSubtyping;
import yamtl.core.YAMTLModule;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EAnnotation;

class YamtlCounter extends YAMTLModule {
	public YamtlCounter() {
		this.selectedExecutionPhases = ExecutionPhase.HELPER_ONLY;
		header().in("in", EcorePackage.eINSTANCE);
	}
}

public class ModelElementsFetcher {

	// function get counts of each EcorePackage literal
	public static HashMap<String, Integer> getCountOfAllLiterals(String modelFilePath) {
		YamtlCounter countEngine = new YamtlCounter();
		countEngine.loadInputModels(Map.of("in", modelFilePath));
		countEngine.execute();

		HashMap<String, Integer> result = new HashMap<String, Integer>();
		result.put("classes", countEngine.allInstances(EcorePackage.eINSTANCE.getEClass()).size());

		// get classes without dependencies
		Integer countOfClassesWithoutDependencies = 0;
		for (Object object: countEngine.allInstances(EcorePackage.eINSTANCE.getEClass()).toArray()) {
			EClass eclass = (EClass) object;
			List<EAnnotation> annotations = eclass.getEAnnotations();
			Boolean dependencyFound = false;
			for (EAnnotation annotation: annotations) {
				if (annotation.getSource().equals("dependency")) {
					dependencyFound = true;
				}
			}
			if (!dependencyFound) {
				countOfClassesWithoutDependencies = countOfClassesWithoutDependencies + 1;
			}
		}
		result.put("classesWithoutDependencies", countOfClassesWithoutDependencies);


		result.put("attributes",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEAttribute()).size());
		result.put("references",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEReference()).size());
		result.put("operations",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEOperation()).size());
		result.put("parameters",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEParameter()).size());
		result.put("enumerations",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEEnum()).size());
		return result;
	}

	// function get counts of each EcorePackage literal
	public static HashMap<String, Object[]> getAllLiterals(String modelFilePath) {
		YamtlCounter countEngine = new YamtlCounter();
		countEngine.loadInputModels(Map.of("in", modelFilePath));
		countEngine.execute();
		HashMap<String, Object[]> result = new HashMap<String, Object[]>();
		result.put("classes", countEngine.allInstances(EcorePackage.eINSTANCE.getEClass()).toArray());
		
		// get classes without dependencies
		ArrayList<Object> classesWithoutDependencies = new ArrayList<Object>();
		for (Object object: countEngine.allInstances(EcorePackage.eINSTANCE.getEClass()).toArray()) {
			EClass eclass = (EClass) object;
			List<EAnnotation> annotations = eclass.getEAnnotations();
			Boolean dependencyFound = false;
			for (EAnnotation annotation: annotations) {
				if (annotation.getSource().equals("dependency")) {
					dependencyFound = true;
				}
			}
			if (!dependencyFound) {
				classesWithoutDependencies.add(object);
			}
		}		
		result.put("classesWithoutDependencies", (Object[]) classesWithoutDependencies.toArray());
		
		result.put("attributes",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEAttribute()).toArray());
		result.put("references",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEReference()).toArray());
		result.put("operations",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEOperation()).toArray());
		result.put("parameters",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEParameter()).toArray());
		result.put("enumerations",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEEnum()).toArray());
		return result;
	}	

	// use yamtl's pattern matching to compute differences and build on that.

	// case
	private static void println(Object text) {
		System.out.println(text);
	}
}