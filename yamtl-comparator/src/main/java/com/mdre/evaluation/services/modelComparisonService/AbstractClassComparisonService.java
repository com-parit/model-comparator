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

import java.util.ArrayList;
import java.util.HashMap;

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

abstract class AbstractClassComparisonService {
	public abstract ArrayList<HashMap<String, String>> getComparableObjectArrayForEAtrributes(List<EAttribute> eAttributesArray);
	public abstract ArrayList<HashMap<String, String>> getComparableObjectArrayForEnums(Object[] enumsArray);
	public abstract ArrayList<HashMap<String, String>> getComparableObjectArrayForEClasses(Object[] enumsArray);
	public abstract ArrayList<HashMap<String, String>> getComparableObjectArrayForEClasses(List<EClass> eClassesArray);
	public abstract ArrayList<HashMap<String, String>> getComparableObjectArrayForEParameters(List<EParameter> eParametersArray);
	public abstract ArrayList<HashMap<String, String>> getComparableObjectArrayForEOperations(List<EOperation> eOperationsArray);
	public abstract ArrayList<HashMap<String, String>> getComparableObjectArrayForEReferences(List<EReference> eReferencesArray);
    public abstract HashMap<String, Object> computeSimilarity(ArrayList<HashMap<String, String>> originalDigest, ArrayList<HashMap<String, String>> predictedDigest);
}