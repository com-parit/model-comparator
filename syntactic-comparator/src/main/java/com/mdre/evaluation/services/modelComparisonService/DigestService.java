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
import com.mdre.evaluation.dtos.VenDiagramDTO;
import com.mdre.evaluation.dtos.MatchedElementsDTO;

public class DigestService extends AbstractClassComparisonService {
	private DigestConfigurationDTO configuration;

	public DigestService(DigestConfigurationDTO configuration) {
		this.configuration = configuration;
	}

	public HashMap<String, String> getComparableObjectForEReference(Object obj) {
		EReference eref = (EReference) obj;
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_REFERENCES_NAME) {
			digest.put("name", eref.getName() != null ? eref.getName().toLowerCase() : "");
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
			digest.put("containingClass", eref.getEContainingClass().getName() != null ? eref.getEContainingClass().getName().toLowerCase() : "");
		}
		if (configuration.INCLUDE_REFERENCES_IS_ORDERED) {
			digest.put("order", Boolean.toString(eref.isOrdered()));
		}
		if (configuration.INCLUDE_REFERENCES_IS_UNIQUE) {
			digest.put("unique", Boolean.toString(eref.isUnique()));
		}
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEClass(Object obj) {
		EClass eclass = (EClass) obj;
		HashMap<String, String> digest = new HashMap<String, String>();
		digest.put("name", eclass.getName() != null ? eclass.getName().toLowerCase() : "");
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEnum(Object obj) {
		EEnum enumeration = (EEnum) obj;
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_ENUM_NAME) {
			digest.put("name", enumeration.getName() != null ? enumeration.getName().toLowerCase() : "");
		}
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEAttribute(Object obj) {
		EAttribute eAtt = (EAttribute) obj;
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_ATTRIBUTE_NAME) {
			digest.put("name", eAtt.getName() != null ? eAtt.getName().toLowerCase() : "");
		}
		if (configuration.INCLUDE_ATTRIBUTE_CONTAINING_CLASS) {
			if (eAtt.getEContainingClass() != null) {
				digest.put("containerClassName", eAtt.getEContainingClass().getName() != null ? eAtt.getEContainingClass().getName().toLowerCase() : "");
			}
		}
		if (configuration.INCLUDE_ATTRIBUTE_TYPE) {
			if (eAtt.getEAttributeType() != null) {
				digest.put("attributeType", eAtt.getEAttributeType().getName() != null ? eAtt.getEAttributeType().getName().toLowerCase() : "");
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

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEParameters(List<EParameter> eParametersArray) {
	 	ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
	 	for(EParameter eparam: eParametersArray) {
	 		HashMap<String, String> digest = getComparableObjectForEParameter(eparam);
	 		arrayOfDigests.add(digest);
	 	}
	 	return arrayOfDigests;
	}
	
	public HashMap<String, String> getComparableObjectForEOperation(Object obj) {
		EOperation eop = (EOperation) obj;
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_OPERATION_NAME) {
			digest.put("name", eop.getName().toLowerCase());
		}
		if (configuration.INCLUDE_OPERATION_CONTAINING_CLASS) {
			if (eop.getEContainingClass() != null) {
				digest.put("containerClassName", eop.getEContainingClass().getName() != null ? eop.getEContainingClass().getName().toLowerCase() : "");
			}
		}
		if (configuration.INCLUDE_OPERATION_PARAMETERS) {
			ArrayList<HashMap<String, String>> parametersDigest = getComparableObjectArrayForEParameters(eop.getEParameters());
			Collections.sort(parametersDigest, Comparator.comparing((HashMap<String, String> map) -> map.get("name")));
			digest.put("parametersDigest", parametersDigest.toString());
		}
		return digest;
	}

	public HashMap<String, String> getComparableObjectForEParameter(Object obj) {
		EParameter eparam = (EParameter) obj;
		HashMap<String, String> digest = new HashMap<String, String>();
		if (configuration.INCLUDE_PARAMETER_NAME) {
			digest.put("name", eparam.getName().toLowerCase());
		}
		if (configuration.INCLUDE_PARAMETER_TYPE) {
			if (eparam.getEType() != null) {
				digest.put("type", eparam.getEType().getName() != null ? eparam.getEType().getName().toLowerCase() : "");
			}
		}
		if (configuration.INCLUDE_PARAMETER_OPERATION_NAME) {
			if (eparam.getEOperation() != null) {
				digest.put("containingMethodName", eparam.getEOperation().getName() != null ? eparam.getEOperation().getName().toLowerCase() : "");
			}		
		}
		return digest;
	}

	@FunctionalInterface
	interface ComparisonFunction {
		HashMap<String, String> apply(Object t);
	}

	public <T> VenDiagramDTO<T> computeIntersection(List<T> model1elements, List<T> model2elements, ComparisonFunction processor) {
		VenDiagramDTO<T> result = new VenDiagramDTO<T>();
		ArrayList<MatchedElementsDTO<T>> intersection = new ArrayList<MatchedElementsDTO<T>>();
		ArrayList<T> onlyInModel1 = new ArrayList<T>();
		ArrayList<T> onlyInModel2 = new ArrayList<T>();

		HashMap<String, T> digestIndexModel1 = new HashMap<String, T>();
		HashMap<String, T> digestIndexModel2 = new HashMap<String, T>();

		ArrayList<HashMap<String, String>> model1ElementsDigest = new ArrayList<HashMap<String, String>>();
		for(T obj: model1elements) {
			HashMap<String, String> digest = processor.apply(obj);
			model1ElementsDigest.add(digest);
			digestIndexModel1.put(digest.toString(), obj);
		}

		ArrayList<HashMap<String, String>> model2ElementsDigest = new ArrayList<HashMap<String, String>>();
		for(T obj: model2elements) {
			HashMap<String, String> digest = processor.apply(obj);
			model2ElementsDigest.add(digest);
			digestIndexModel2.put(digest.toString(), obj);
		}

        for (HashMap<String, String> digest1 : model1ElementsDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest2 : model2ElementsDigest) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
					MatchedElementsDTO<T> matchedElements = new MatchedElementsDTO<T>();
					matchedElements.model1 = digestIndexModel1.get(digest1.toString());
					matchedElements.model2 = digestIndexModel2.get(digest2.toString());
                    intersection.add(matchedElements);
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel1.add(digestIndexModel1.get(digest1.toString()));
            }
        }

        for (HashMap<String, String> digest2 : model2ElementsDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest1 : model1ElementsDigest) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(digestIndexModel2.get(digest2.toString()));
            }
        }

		result.matched = intersection;
		result.onlyInModel1 = onlyInModel1;
		result.onlyInModel2 = onlyInModel2;
		return result;		
	}

	public VenDiagramDTO<EClass> getVenDiagramForClasses(List<EClass> classesModel1, List<EClass> classesModel2) {
		VenDiagramDTO<EClass> result = computeIntersection(classesModel1, classesModel2, (obj) -> getComparableObjectForEClass(obj));
		return result;
	}

	public VenDiagramDTO<EEnum> getVenDiagramForEnumerations(List<EEnum> enumsModel1, List<EEnum> enumsModel2) {
		VenDiagramDTO<EEnum> result = computeIntersection(enumsModel1, enumsModel2, (obj) -> getComparableObjectForEnum(obj));
		return result;
	}

	public VenDiagramDTO<EAttribute> getVenDiagramForEAttributes(List<EAttribute> attributesClass1, List<EAttribute> attributesClass2) {
		VenDiagramDTO<EAttribute> result = computeIntersection(attributesClass1, attributesClass2, (obj) -> getComparableObjectForEAttribute(obj));
		return result;
	}

	public VenDiagramDTO<EReference> getVenDiagramForEReferences(List<EReference> referencesClass1, List<EReference> referencesClass2) {
		VenDiagramDTO<EReference> result = computeIntersection(referencesClass1, referencesClass2, (obj) -> getComparableObjectForEReference(obj));
		return result;
	}

	public VenDiagramDTO<EOperation> getVenDiagramForEOperations(List<EOperation> eoperationsClass1, List<EOperation> eoperationsClass2) {
		VenDiagramDTO<EOperation> result = computeIntersection(eoperationsClass1, eoperationsClass2, (obj) -> getComparableObjectForEOperation(obj));
		return result;
	}
}