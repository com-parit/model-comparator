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

import com.mdre.evaluation.services.modelComparisonService.AbstractClassComparisonService;
import com.mdre.evaluation.config.Constants;
import com.mdre.evaluation.dtos.DigestConfigurationDTO;

public class DigestService extends AbstractClassComparisonService {
	private DigestConfigurationDTO configuration;

	public DigestService(DigestConfigurationDTO configuration) {
		this.configuration = configuration;
	}

	public HashMap<String, String> getComparableObjectForEReference(EReference eref) {
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_REFERENCES_NAME) {
			digest.put("name", eref.getName().toLowerCase());
		}
		if (configuration.INCLUDE_REFERENCES_LOWER_BOUND) {
			digest.put("lowerBound", Integer.toString(eref.getLowerBound()));
		}
		if (configuration.INCLUDE_REFERENCES_UPPER_BOUND) {
			digest.put("upperBound", Integer.toString(eref.getUpperBound()));
		}
		if (configuration.INCLUDE_REFERENCES_IS_CONTAINMENT) {
			digest.put("isContainment", Boolean.toString(eref.isContainment()));
		}
		if (configuration.INCLUDE_REFERENCES_CONTAINING_CLASS) {
			digest.put("containingClass", eref.getEContainingClass().getName().toLowerCase());
		}
		if (configuration.INCLUDE_REFERENCES_IS_ORDERED) {
			digest.put("order", Boolean.toString(eref.isOrdered()));
		}
		if (configuration.INCLUDE_REFERENCES_IS_UNIQUE) {
			digest.put("unique", Boolean.toString(eref.isUnique()));
		}
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEClass(EClass eClass) {
		HashMap<String, String> digest = new HashMap<String, String>();
		digest.put("name", eClass.getName().toLowerCase());
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEnum(EEnum enumeration) {
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_ENUM_NAME) {
			digest.put("name", enumeration.getName().toLowerCase());
		}
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEAttribute(EAttribute eAtt) {
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_ATTRIBUTE_NAME) {
			digest.put("name", eAtt.getName().toLowerCase());
		}
		if (configuration.INCLUDE_ATTRIBUTE_CONTAINING_CLASS) {
			if (eAtt.getEContainingClass() != null) {
				digest.put("containerClassName", eAtt.getEContainingClass().getName().toLowerCase());
			}
		}
		if (configuration.INCLUDE_ATTRIBUTE_TYPE) {
			if (eAtt.getEAttributeType() != null) {
				digest.put("attributeType", eAtt.getEAttributeType().getName().toLowerCase());
			}
		}
		if (configuration.INCLUDE_ATTRIBUTE_LOWER_BOUND) {
			digest.put("lowerBound", Integer.toString(eAtt.getLowerBound()));
		}
		if (configuration.INCLUDE_ATTRIBUTE_UPPER_BOUND) {
			digest.put("upperBound", Integer.toString(eAtt.getUpperBound()));
		}
		if (configuration.INCLUDE_ATTRIBUTE_IS_ORDERED) {
			digest.put("order", Boolean.toString(eAtt.isOrdered()));
		}
		if (configuration.INCLUDE_ATTRIBUTE_IS_UNIQUE) {
			digest.put("unique", Boolean.toString(eAtt.isUnique()));
		}
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEoperation(EOperation eop) {
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_OPERATION_NAME) {
			digest.put("name", eop.getName().toLowerCase());
		}
		if (configuration.INCLUDE_OPERATION_CONTAINING_CLASS) {
			if (eop.getEContainingClass() != null) {
				digest.put("containerClassName", eop.getEContainingClass().getName().toLowerCase());
			}
		}
		if (configuration.INCLUDE_OPERATION_PARAMETERS) {
			ArrayList<HashMap<String, String>> parametersDigest = getComparableObjectArrayForEParameters(eop.getEParameters());
			Collections.sort(parametersDigest, Comparator.comparing((HashMap<String, String> map) -> map.get("name")));
			digest.put("parametersDigest", parametersDigest.toString());
		}
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEParameter(EParameter eparam) {
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_PARAMETER_NAME) {
			digest.put("name", eparam.getName().toLowerCase());
		}
		if (configuration.INCLUDE_PARAMETER_TYPE) {
			if (eparam.getEType() != null) {
				digest.put("type", eparam.getEType().getName().toLowerCase());
			}
		}
		if (configuration.INCLUDE_PARAMETER_OPERATION_NAME) {
			if (eparam.getEOperation() != null) {
				digest.put("containingMethodName", eparam.getEOperation().getName().toLowerCase());
			}		
		}
		return digest;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEReferences(List<EReference> eReferencesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EReference eref: eReferencesArray) {
			HashMap<String, String> digest = getComparableObjectForEReference(eref);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEClasses(List<EClass> eClassesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EClass eclass: eClassesArray) {
			HashMap<String, String> digest = getComparableObjectForEClass(eclass);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEnums(Object[] enumsArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(Object object: enumsArray) {
			EEnum enumObject = (EEnum) object; 
			HashMap<String, String> digest = getComparableObjectForEnum(enumObject);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEClasses(Object[] eClassesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(Object a: eClassesArray) {
			EClass eclass = (EClass) a;
			HashMap<String, String> digest = getComparableObjectForEClass(eclass);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEAtrributes(List<EAttribute> eAttributesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EAttribute eAtt: eAttributesArray) {
			HashMap<String, String> digest = getComparableObjectForEAttribute(eAtt);
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	 public ArrayList<HashMap<String, String>> getComparableObjectArrayForEOperations(List<EOperation> eOperationsArray) {
	 	ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
	 	for(Object a: eOperationsArray) {
	 		EOperation eop = (EOperation) a;
	 		HashMap<String, String> digest = getComparableObjectForEoperation(eop);
			arrayOfDigests.add(digest);
	 	}
	 	return arrayOfDigests;
	 }

	 public ArrayList<HashMap<String, String>> getComparableObjectArrayForEParameters(List<EParameter> eParametersArray) {
	 	ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
	 	for(EParameter eparam: eParametersArray) {
	 		HashMap<String, String> digest = getComparableObjectForEParameter(eparam);
	 		arrayOfDigests.add(digest);
	 	}
	 	return arrayOfDigests;
	 }	

    public HashMap<String, Object> computeSimilarity(
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