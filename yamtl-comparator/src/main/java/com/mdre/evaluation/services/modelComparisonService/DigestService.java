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

import org.eclipse.emf.ecore.EcorePackage;

import subtyping.EMFSubtyping;
import yamtl.core.YAMTLModule;

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

public class DigestService {

	public static HashMap<String, String> getDigestForEReference(EReference eref) {
		HashMap<String, String> digest = new HashMap<String, String>();
		digest.put("name", eref.getName().toLowerCase());
		digest.put("lowerBound", Integer.toString(eref.getLowerBound()));
		digest.put("upperBound", Integer.toString(eref.getUpperBound()));
		digest.put("isContainment", Boolean.toString(eref.isContainment()));
		digest.put("containingClass", eref.getEContainingClass().getName().toLowerCase());
		digest.put("order", Boolean.toString(eref.isOrdered()));
		digest.put("unique", Boolean.toString(eref.isUnique()));
		return digest;
	}

	public static HashMap<String, String> getDigsetForEClass(EClass eClass) {
		HashMap<String, String> digest = new HashMap<String, String>();
		digest.put("name", eClass.getName().toLowerCase());
		// digest.put("instanceClassName", eClass.getInstanceClassName());
		// digest.put("abstract", Boolean.toString(eClass.isAbstract()));
		// digest.put("interface", Boolean.toString(eClass.isInterface()));
		return digest;
	}

	public static HashMap<String, String> getDigestForEnum(EEnum enumeration) {
		HashMap<String, String> digest = new HashMap<String, String>();
		digest.put("name", enumeration.getName().toLowerCase());
		// digest.put("instanceClassName", eClass.getInstanceClassName());
		// digest.put("abstract", Boolean.toString(eClass.isAbstract()));
		// digest.put("interface", Boolean.toString(eClass.isInterface()));
		return digest;
	}

	public static HashMap<String, String> getDigestForEAttribute(EAttribute eAtt) {
		HashMap<String, String> digest = new HashMap<String, String>();
		digest.put("name", eAtt.getName().toLowerCase());
		if (eAtt.getEContainingClass() != null) {
			digest.put("containerClassName", eAtt.getEContainingClass().getName().toLowerCase());
		}
		if (eAtt.getEAttributeType() != null) {
			digest.put("attributeType", eAtt.getEAttributeType().getName().toLowerCase());
		}
		digest.put("lowerBound", Integer.toString(eAtt.getLowerBound()));
		digest.put("upperBound", Integer.toString(eAtt.getUpperBound()));
		digest.put("order", Boolean.toString(eAtt.isOrdered()));
		digest.put("unique", Boolean.toString(eAtt.isUnique()));
		return digest;
	}

	public static HashMap<String, String> getDigestForEoperation(EOperation eop) {
		HashMap<String, String> digest = new HashMap<String, String>();
		digest.put("name", eop.getName().toLowerCase());
		if (eop.getEContainingClass() != null) {
			digest.put("containerClassName", eop.getEContainingClass().getName().toLowerCase());
		}
		ArrayList<HashMap<String, String>> parametersDigest = getDigestArrayForEParameters(eop.getEParameters());
		Collections.sort(parametersDigest, Comparator.comparing((HashMap<String, String> map) -> map.get("name")));
		digest.put("parametersDigest", parametersDigest.toString());
		return digest;
	}

	public static HashMap<String, String> getDigestForEParameter(EParameter eparam) {
		HashMap<String, String> digest = new HashMap<String, String>();
		digest.put("name", eparam.getName().toLowerCase());
		if (eparam.getEType() != null) {
			digest.put("type", eparam.getEType().getName().toLowerCase());
		}
		if (eparam.getEOperation() != null) {
			digest.put("containingMethodName", eparam.getEOperation().getName().toLowerCase());
		}		
		return digest;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEReferences(List<EReference> eReferencesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EReference eref: eReferencesArray) {
			HashMap<String, String> digest = getDigestForEReference(eref);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEClasses(List<EClass> eClassesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EClass eclass: eClassesArray) {
			HashMap<String, String> digest = getDigsetForEClass(eclass);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEnums(Object[] enumsArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(Object object: enumsArray) {
			EEnum enumObject = (EEnum) object; 
			HashMap<String, String> digest = getDigestForEnum(enumObject);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEClasses(Object[] eClassesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(Object a: eClassesArray) {
			EClass eclass = (EClass) a;
			HashMap<String, String> digest = getDigsetForEClass(eclass);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEAtrributes(List<EAttribute> eAttributesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EAttribute eAtt: eAttributesArray) {
			HashMap<String, String> digest = getDigestForEAttribute(eAtt);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	 public static ArrayList<HashMap<String, String>> getDigestArrayForEOperations(List<EOperation> eOperationsArray) {
	 	ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
	 	for(Object a: eOperationsArray) {
	 		EOperation eop = (EOperation) a;
	 		HashMap<String, String> digest = getDigestForEoperation(eop);
			arrayOfDigests.add(digest);
	 	}
	 	return arrayOfDigests;
	 }

	 public static ArrayList<HashMap<String, String>> getDigestArrayForEParameters(List<EParameter> eParametersArray) {
	 	ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
	 	for(EParameter eparam: eParametersArray) {
	 		HashMap<String, String> digest = getDigestForEParameter(eparam);
	 		arrayOfDigests.add(digest);
	 	}
	 	return arrayOfDigests;
	 }	

    public static HashMap<String, Object> computeSimilarityForDigests(
		ArrayList<HashMap<String, String>> originalDigest, 
		ArrayList<HashMap<String, String>> predictedDigest
		) {
        Integer truePositives = 0;
        Integer falsePositives = 0;
        Integer falseNegatives = 0;

        for (HashMap<String, String> digest1 : originalDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest2 : predictedDigest) {
                if (digest1.equals(digest2)) {
                    truePositives++;
                    matchFound = true;
					String description = "\npredicted: " + digest2.toString();
                    break;
                }
            }
            if (!matchFound) {
				String description = "original entity not found in predicted.\n ";
                falseNegatives++;
            }
        }
        falsePositives = predictedDigest.size() - truePositives;

		HashMap<String, Integer> confusionMatrix = new HashMap<String, Integer>();
		confusionMatrix.put("tp", truePositives);
		confusionMatrix.put("fp", falsePositives);
		confusionMatrix.put("fn", falseNegatives);

		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("confusionMatrix", confusionMatrix);
        return result;
    }
}