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
import com.mdre.evaluation.dtos.MatchedClassesDTO;
import com.mdre.evaluation.dtos.VenDiagramClassesDTO;
import com.mdre.evaluation.dtos.VenDiagramEEnumsDTO;
import com.mdre.evaluation.dtos.MatchedEEnumsDTO;
import com.mdre.evaluation.dtos.VenDiagramEAttributesDTO;
import com.mdre.evaluation.dtos.MatchedEAttributesDTO;
import com.mdre.evaluation.dtos.VenDiagramEReferencesDTO;
import com.mdre.evaluation.dtos.MatchedEReferencesDTO;
import com.mdre.evaluation.dtos.VenDiagramEOperationsDTO;
import com.mdre.evaluation.dtos.MatchedEOperationsDTO;

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

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEParameters(List<EParameter> eParametersArray) {
	 	ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
	 	for(EParameter eparam: eParametersArray) {
	 		HashMap<String, String> digest = getComparableObjectForEParameter(eparam);
	 		arrayOfDigests.add(digest);
	 	}
	 	return arrayOfDigests;
	}
	
	public HashMap<String, String> getComparableObjectForEOperation(EOperation eop) {
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

	public VenDiagramClassesDTO getVenDiagramForClasses(List<EClass> classesModel1, List<EClass> classesModel2) {
		VenDiagramClassesDTO result = new VenDiagramClassesDTO();
		ArrayList<MatchedClassesDTO> intersection = new ArrayList<MatchedClassesDTO>();
		ArrayList<EClass> onlyInModel1 = new ArrayList<EClass>();
		ArrayList<EClass> onlyInModel2 = new ArrayList<EClass>();

		for (EClass eclassOriginal: classesModel1) {
			Boolean matched = false;
			for (EClass eclassPredicted: classesModel2) {
				if (eclassOriginal.getName().equals(eclassPredicted.getName())) {
					MatchedClassesDTO matchedClasses = new MatchedClassesDTO();
					matchedClasses.model1 = eclassOriginal;
					matchedClasses.model2 = eclassPredicted;
					intersection.add(matchedClasses);
					matched = true;
				}
			}
			if (!matched) {
				onlyInModel1.add(eclassOriginal);
			}
		}

		for (EClass eclassPredicted: classesModel2) {
			Boolean matched = false;
			for (EClass eclassOriginal: classesModel1) {
				if (eclassOriginal.getName() == eclassPredicted.getName()) {
					matched = true;
				}
			}
			if (!matched) {
				onlyInModel2.add(eclassPredicted);
			}
		}
		result.matched = intersection;
		result.onlyInModel1 = onlyInModel1;
		result.onlyInModel2 = onlyInModel2;
		return result;
	}

	public VenDiagramEEnumsDTO getVenDiagramForEnumerations(List<EEnum> enumsModel1, List<EEnum> enumsModel2) {
		VenDiagramEEnumsDTO result = new VenDiagramEEnumsDTO();
		ArrayList<MatchedEEnumsDTO> intersection = new ArrayList<MatchedEEnumsDTO>();
		ArrayList<EEnum> onlyInModel1 = new ArrayList<EEnum>();
		ArrayList<EEnum> onlyInModel2 = new ArrayList<EEnum>();

		HashMap<String, EEnum> digestIndexModel1 = new HashMap<String, EEnum>();
		HashMap<String, EEnum> digestIndexModel2 = new HashMap<String, EEnum>();

		ArrayList<HashMap<String, String>> model1EEnumsDigest = new ArrayList<HashMap<String, String>>();
		for(EEnum eenum: enumsModel1) {
			HashMap<String, String> digest = getComparableObjectForEnum(eenum);
			model1EEnumsDigest.add(digest);
			digestIndexModel1.put(digest.toString(), eenum);
		}

		ArrayList<HashMap<String, String>> model2EEnumsDigest = new ArrayList<HashMap<String, String>>();
		for(EEnum eenum: enumsModel2) {
			HashMap<String, String> digest = getComparableObjectForEnum(eenum);
			model2EEnumsDigest.add(digest);
			digestIndexModel2.put(digest.toString(), eenum);
		}

        for (HashMap<String, String> digest1 : model1EEnumsDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest2 : model2EEnumsDigest) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
					MatchedEEnumsDTO matchedEnums = new MatchedEEnumsDTO();
					matchedEnums.model1 = digestIndexModel1.get(digest1.toString());
					matchedEnums.model2 = digestIndexModel2.get(digest2.toString());
                    intersection.add(matchedEnums);
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel1.add(digestIndexModel1.get(digest1.toString()));
            }
        }

        for (HashMap<String, String> digest2 : model2EEnumsDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest1 : model1EEnumsDigest) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(digestIndexModel2.get(digest2));
            }
        }

		result.matched = intersection;
		result.onlyInModel1 = onlyInModel1;
		result.onlyInModel2 = onlyInModel2;
		return result;
	}

	public VenDiagramEAttributesDTO getVenDiagramForEAttributes(List<EAttribute> attributesClass1, List<EAttribute> attributesClass2) {
		VenDiagramEAttributesDTO result = new VenDiagramEAttributesDTO();
		ArrayList<MatchedEAttributesDTO> intersection = new ArrayList<MatchedEAttributesDTO>();
		ArrayList<EAttribute> onlyInModel1 = new ArrayList<EAttribute>();
		ArrayList<EAttribute> onlyInModel2 = new ArrayList<EAttribute>();

		HashMap<String, EAttribute> digestIndexModel1 = new HashMap<String, EAttribute>();
		HashMap<String, EAttribute> digestIndexModel2 = new HashMap<String, EAttribute>();

		ArrayList<HashMap<String, String>> model1eAttributesDigest = new ArrayList<HashMap<String, String>>();
		for(EAttribute eAttribute: attributesClass1) {
			HashMap<String, String> digest = getComparableObjectForEAttribute(eAttribute);
			model1eAttributesDigest.add(digest);
			digestIndexModel1.put(digest.toString(), eAttribute);
		}

		ArrayList<HashMap<String, String>> model2eAttributesDigest = new ArrayList<HashMap<String, String>>();
		for(EAttribute eAttribute: attributesClass2) {
			HashMap<String, String> digest = getComparableObjectForEAttribute(eAttribute);
			model2eAttributesDigest.add(digest);
			digestIndexModel2.put(digest.toString(), eAttribute);
		}

        for (HashMap<String, String> digest1 : model1eAttributesDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest2 : model2eAttributesDigest) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
					MatchedEAttributesDTO matchedAttributes = new MatchedEAttributesDTO();
					matchedAttributes.model1 = digestIndexModel1.get(digest1.toString());
					matchedAttributes.model2 = digestIndexModel2.get(digest2.toString());
                    intersection.add(matchedAttributes);
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel1.add(digestIndexModel1.get(digest1.toString()));
            }
        }

        for (HashMap<String, String> digest2 : model2eAttributesDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest1 : model1eAttributesDigest) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(digestIndexModel2.get(digest2));
            }
        }

		result.matched = intersection;
		result.onlyInModel1 = onlyInModel1;
		result.onlyInModel2 = onlyInModel2;
		return result;
	}

	public VenDiagramEReferencesDTO getVenDiagramForEReferences(List<EReference> referencesClass1, List<EReference> referencesClass2) {
		VenDiagramEReferencesDTO result = new VenDiagramEReferencesDTO();
		ArrayList<MatchedEReferencesDTO> intersection = new ArrayList<MatchedEReferencesDTO>();
		ArrayList<EReference> onlyInModel1 = new ArrayList<EReference>();
		ArrayList<EReference> onlyInModel2 = new ArrayList<EReference>();

		HashMap<String, EReference> digestIndexModel1 = new HashMap<String, EReference>();
		HashMap<String, EReference> digestIndexModel2 = new HashMap<String, EReference>();

		ArrayList<HashMap<String, String>> model1EReferencesDigest = new ArrayList<HashMap<String, String>>();
		for(EReference obj: referencesClass1) {
			HashMap<String, String> digest = getComparableObjectForEReference(obj);
			model1EReferencesDigest.add(digest);
			digestIndexModel1.put(digest.toString(), obj);
		}

		ArrayList<HashMap<String, String>> model2EReferencesDigest = new ArrayList<HashMap<String, String>>();
		for(EReference obj: referencesClass2) {
			HashMap<String, String> digest = getComparableObjectForEReference(obj);
			model2EReferencesDigest.add(digest);
			digestIndexModel2.put(digest.toString(), obj);
		}

        for (HashMap<String, String> digest1 : model1EReferencesDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest2 : model2EReferencesDigest) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
					MatchedEReferencesDTO matchedElements = new MatchedEReferencesDTO();
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

        for (HashMap<String, String> digest2 : model2EReferencesDigest) {
            boolean matchFound = false;
            for (HashMap<String, String> digest1 : model1EReferencesDigest) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(digestIndexModel2.get(digest2));
            }
        }

		result.matched = intersection;
		result.onlyInModel1 = onlyInModel1;
		result.onlyInModel2 = onlyInModel2;
		return result;
	}

	public VenDiagramEOperationsDTO getVenDiagramForEOperations(List<EOperation> eoperationsClass1, List<EOperation> eoperationsClass2) {
		VenDiagramEOperationsDTO result = new VenDiagramEOperationsDTO();
		ArrayList<MatchedEOperationsDTO> intersection = new ArrayList<MatchedEOperationsDTO>();
		ArrayList<EOperation> onlyInModel1 = new ArrayList<EOperation>();
		ArrayList<EOperation> onlyInModel2 = new ArrayList<EOperation>();

		HashMap<String, EOperation> digestIndexModel1 = new HashMap<String, EOperation>();
		HashMap<String, EOperation> digestIndexModel2 = new HashMap<String, EOperation>();

		ArrayList<HashMap<String, String>> model1Digests = new ArrayList<HashMap<String, String>>();
		for(EOperation obj: eoperationsClass1) {
			HashMap<String, String> digest = getComparableObjectForEOperation(obj);
			model1Digests.add(digest);
			digestIndexModel1.put(digest.toString(), obj);
		}

		ArrayList<HashMap<String, String>> model2Digests = new ArrayList<HashMap<String, String>>();
		for(EOperation obj: eoperationsClass2) {
			HashMap<String, String> digest = getComparableObjectForEOperation(obj);
			model2Digests.add(digest);
			digestIndexModel2.put(digest.toString(), obj);
		}

        for (HashMap<String, String> digest1 : model1Digests) {
            boolean matchFound = false;
            for (HashMap<String, String> digest2 : model2Digests) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
					MatchedEOperationsDTO matchedElements = new MatchedEOperationsDTO();
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

        for (HashMap<String, String> digest2 : model2Digests) {
            boolean matchFound = false;
            for (HashMap<String, String> digest1 : model1Digests) {
                if (digest1.equals(digest2)) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(digestIndexModel2.get(digest2));
            }
        }

		result.matched = intersection;
		result.onlyInModel1 = onlyInModel1;
		result.onlyInModel2 = onlyInModel2;
		return result;
	}
}