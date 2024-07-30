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
import java.util.zip.CRC32;
import org.json.JSONObject;

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

import com.mdre.evaluation.services.modelComparisonService.AbstractClassComparisonService;
import com.mdre.evaluation.config.Constants;
import com.mdre.evaluation.dtos.HashingConfigurationDTO;
import com.mdre.evaluation.dtos.VenDiagramClassesDTO;
import com.mdre.evaluation.dtos.MatchedClassesDTO;
import com.mdre.evaluation.dtos.VenDiagramEEnumsDTO;
import com.mdre.evaluation.dtos.MatchedEEnumsDTO;
import com.mdre.evaluation.dtos.VenDiagramEAttributesDTO;
import com.mdre.evaluation.dtos.MatchedEAttributesDTO;
import com.mdre.evaluation.dtos.VenDiagramEReferencesDTO;
import com.mdre.evaluation.dtos.MatchedEReferencesDTO;
import com.mdre.evaluation.dtos.VenDiagramEOperationsDTO;
import com.mdre.evaluation.dtos.MatchedEOperationsDTO;

public class HashingService extends AbstractClassComparisonService {
	private HashingConfigurationDTO hashingConfiguration;

	public HashingService(HashingConfigurationDTO configuration) {
		this.hashingConfiguration = configuration;
	}

    public static long computeCRC32(String input) {
            CRC32 crc32 = new CRC32();
            crc32.update(input.getBytes());
            return crc32.getValue();
	}

    public static int hammingDistance(String hash1, String hash2) {
        int maxLength = Math.max(hash1.length(), hash2.length());
        int distance = 0;
        for (int i = 0; i < maxLength; i++) {
            char bit1 = (i < hash1.length()) ? hash1.charAt(i) : '0';
            char bit2 = (i < hash2.length()) ? hash2.charAt(i) : '0';
            if (bit1 != bit2) {
                distance++;
            }
        }
        return distance;
    }

    public static double normalizedHammingDistance(String hash1, String hash2) {
        int distance = hammingDistance(hash1, hash2);
        int maxLength = Math.max(hash1.length(), hash2.length());
        return (double) distance / maxLength;
    }

	public String getComparableObjectForEReference(EReference eref) {
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_REFERENCES_NAME) {
	        totalChecksum += computeCRC32(eref.getName().toLowerCase());
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_LOWER_BOUND) {
	        totalChecksum += computeCRC32(Integer.toString(eref.getLowerBound()));
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_UPPER_BOUND) {
	        totalChecksum += computeCRC32(Integer.toString(eref.getUpperBound()));
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_IS_CONTAINMENT) {
	        totalChecksum += computeCRC32(Boolean.toString(eref.isContainment()));
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_CONTAINING_CLASS) {
	        totalChecksum += computeCRC32(eref.getEContainingClass().getName().toLowerCase());
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_IS_ORDERED) {
	        totalChecksum += computeCRC32(Boolean.toString(eref.isOrdered()));
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_IS_UNIQUE) {
	        totalChecksum += computeCRC32(Boolean.toString(eref.isUnique()));
		}
        String binaryHash = Long.toBinaryString(totalChecksum);
        return binaryHash;
	}

	public String getComparableObjectForEClass(EClass eClass) {
        long totalChecksum = 0;
        totalChecksum += computeCRC32(eClass.getName().toLowerCase());
        String binaryHash = Long.toBinaryString(totalChecksum);
        return binaryHash;
	}

	public String getComparableObjectForEnum(EEnum enumeration) {
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_ENUM_NAME) {
	        totalChecksum += computeCRC32(enumeration.getName().toLowerCase());
		}
        String binaryHash = Long.toBinaryString(totalChecksum);
        return binaryHash;
	}

	public String getComparableObjectForEAttribute(EAttribute eAtt) {
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_NAME) {
	        totalChecksum += computeCRC32(eAtt.getName().toLowerCase());
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_CONTAINING_CLASS) {
			if (eAtt.getEContainingClass() != null) {
				totalChecksum += computeCRC32(eAtt.getEContainingClass().getName().toLowerCase());
			}
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_TYPE) {
			if (eAtt.getEAttributeType() != null) {
				totalChecksum += computeCRC32(eAtt.getEAttributeType().getName().toLowerCase());
			}
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_LOWER_BOUND) {
	        totalChecksum += computeCRC32(Integer.toString(eAtt.getLowerBound()));
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_UPPER_BOUND) {
	        totalChecksum += computeCRC32(Integer.toString(eAtt.getUpperBound()));
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_IS_ORDERED) {
	        totalChecksum += computeCRC32(Boolean.toString(eAtt.isOrdered()));
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_IS_UNIQUE) {
	        totalChecksum += computeCRC32(Boolean.toString(eAtt.isUnique()));
		}
        String binaryHash = Long.toBinaryString(totalChecksum);
		return binaryHash;
	}

	public String getComparableObjectForEOperation(EOperation eop) {
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_OPERATION_NAME) {
	        totalChecksum += computeCRC32(eop.getName().toLowerCase());
		}
		if (hashingConfiguration.INCLUDE_OPERATION_CONTAINING_CLASS) {
			if (eop.getEContainingClass() != null) {
				totalChecksum += computeCRC32(eop.getEContainingClass().getName().toLowerCase());
			}
		}
		if (hashingConfiguration.INCLUDE_OPERATION_PARAMETERS) {
			for(EParameter eparam: eop.getEParameters()) {
				long hash = getHashForOperationParameters(eparam);
			    totalChecksum += hash;
			}
		}
        String binaryHash = Long.toBinaryString(totalChecksum);
		return binaryHash;
	}

	public String getComparableObjectForEParameter(EParameter eparam) {
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_PARAMETER_NAME) {
	        totalChecksum += computeCRC32(eparam.getName().toLowerCase());
		}
		if (hashingConfiguration.INCLUDE_PARAMETER_TYPE) {
			if (eparam.getEType() != null) {
				totalChecksum += computeCRC32(eparam.getEType().getName().toLowerCase());
			}
		}
		if (hashingConfiguration.INCLUDE_PARAMETER_OPERATION_NAME) {
			if (eparam.getEOperation() != null) {
				totalChecksum += computeCRC32(eparam.getEOperation().getName().toLowerCase());
			}
		}
        String binaryHash = Long.toBinaryString(totalChecksum);
		return binaryHash;
	}

	public long getHashForOperationParameters(EParameter eparam) {
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_PARAMETER_NAME) {
	        totalChecksum += computeCRC32(eparam.getName().toLowerCase());
		}
		if (hashingConfiguration.INCLUDE_PARAMETER_TYPE) {
			if (eparam.getEType() != null) {
				totalChecksum += computeCRC32(eparam.getEType().getName().toLowerCase());
			}
		}
		if (hashingConfiguration.INCLUDE_PARAMETER_OPERATION_NAME) {
			if (eparam.getEOperation() != null) {
				totalChecksum += computeCRC32(eparam.getEOperation().getName().toLowerCase());
			}
		}
		return totalChecksum;
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

		HashMap<String, EEnum> hashIndexModel1 = new HashMap<String, EEnum>();
		HashMap<String, EEnum> hashIndexModel2 = new HashMap<String, EEnum>();

		ArrayList<String> model1EEnumshash = new ArrayList<String>();
		for(EEnum eenum: enumsModel1) {
			String hash = getComparableObjectForEnum(eenum);
			model1EEnumshash.add(hash);
			hashIndexModel1.put(hash, eenum);
		}

		ArrayList<String> model2EEnumshash = new ArrayList<String>();
		for(EEnum eenum: enumsModel2) {
			String hash = getComparableObjectForEnum(eenum);
			model2EEnumshash.add(hash);
			hashIndexModel2.put(hash, eenum);
		}

        for (String hash1 : model1EEnumshash) {
            boolean matchFound = false;
            for (String hash2 : model2EEnumshash) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    matchFound = true;
					MatchedEEnumsDTO matchedEnums = new MatchedEEnumsDTO();
					matchedEnums.model1 = hashIndexModel1.get(hash1);
					matchedEnums.model2 = hashIndexModel2.get(hash2);
                    intersection.add(matchedEnums);
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel1.add(hashIndexModel1.get(hash1));
            }
        }

        for (String hash2 : model2EEnumshash) {
            boolean matchFound = false;
            for (String hash1 : model1EEnumshash) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(hashIndexModel2.get(hash2));
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

		HashMap<String, EAttribute> hashIndexModel1 = new HashMap<String, EAttribute>();
		HashMap<String, EAttribute> hashIndexModel2 = new HashMap<String, EAttribute>();

		ArrayList<String> model1Hashes = new ArrayList<String>();
		for(EAttribute eAttribute: attributesClass1) {
			String hash = getComparableObjectForEAttribute(eAttribute);
			model1Hashes.add(hash);
			hashIndexModel1.put(hash, eAttribute);
		}

		ArrayList<String> model2Hashes = new ArrayList<String>();
		for(EAttribute eAttribute: attributesClass2) {
			String hash = getComparableObjectForEAttribute(eAttribute);
			model2Hashes.add(hash);
			hashIndexModel2.put(hash, eAttribute);
		}

        for (String hash1 : model1Hashes) {
            boolean matchFound = false;
            for (String hash2 : model2Hashes) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    matchFound = true;
					MatchedEAttributesDTO matchedAttributes = new MatchedEAttributesDTO();
					matchedAttributes.model1 = hashIndexModel1.get(hash1);
					matchedAttributes.model2 = hashIndexModel2.get(hash2);
                    intersection.add(matchedAttributes);
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel1.add(hashIndexModel1.get(hash1));
            }
        }

        for (String hash2 : model2Hashes) {
            boolean matchFound = false;
            for (String hash1 : model1Hashes) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(hashIndexModel2.get(hash2));
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

		HashMap<String, EReference> hashIndexModel1 = new HashMap<String, EReference>();
		HashMap<String, EReference> hashIndexModel2 = new HashMap<String, EReference>();

		ArrayList<String> model1Hashes = new ArrayList<String>();
		for(EReference refObject: referencesClass1) {
			String hash = getComparableObjectForEReference(refObject);
			model1Hashes.add(hash);
			hashIndexModel1.put(hash, refObject);
		}

		ArrayList<String> model2Hashes = new ArrayList<String>();
		for(EReference refObject: referencesClass2) {
			String hash = getComparableObjectForEReference(refObject);
			model2Hashes.add(hash);
			hashIndexModel2.put(hash, refObject);
		}

        for (String hash1 : model1Hashes) {
            boolean matchFound = false;
            for (String hash2 : model2Hashes) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    matchFound = true;
					MatchedEReferencesDTO matchedReferences = new MatchedEReferencesDTO();
					matchedReferences.model1 = hashIndexModel1.get(hash1);
					matchedReferences.model2 = hashIndexModel2.get(hash2);
                    intersection.add(matchedReferences);
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel1.add(hashIndexModel1.get(hash1));
            }
        }

        for (String hash2 : model2Hashes) {
            boolean matchFound = false;
            for (String hash1 : model1Hashes) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(hashIndexModel2.get(hash2));
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

		HashMap<String, EOperation> hashIndexModel1 = new HashMap<String, EOperation>();
		HashMap<String, EOperation> hashIndexModel2 = new HashMap<String, EOperation>();

		ArrayList<String> model1Hashes = new ArrayList<String>();
		for(EOperation obj: eoperationsClass1) {
			String hash = getComparableObjectForEOperation(obj);
			model1Hashes.add(hash);
			hashIndexModel1.put(hash, obj);
		}

		ArrayList<String> model2Hashes = new ArrayList<String>();
		for(EOperation obj: eoperationsClass2) {
			String hash = getComparableObjectForEOperation(obj);
			model2Hashes.add(hash);
			hashIndexModel2.put(hash, obj);
		}

        for (String hash1 : model1Hashes) {
            boolean matchFound = false;
            for (String hash2 : model2Hashes) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    matchFound = true;
					MatchedEOperationsDTO matchedElements = new MatchedEOperationsDTO();
					matchedElements.model1 = hashIndexModel1.get(hash1);
					matchedElements.model2 = hashIndexModel2.get(hash2);
                    intersection.add(matchedElements);
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel1.add(hashIndexModel1.get(hash1));
            }
        }

        for (String hash2 : model2Hashes) {
            boolean matchFound = false;
            for (String hash1 : model1Hashes) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
				onlyInModel2.add(hashIndexModel2.get(hash2));
            }
        }

		result.matched = intersection;
		result.onlyInModel1 = onlyInModel1;
		result.onlyInModel2 = onlyInModel2;
		return result;
	}

    public HashMap<String, Object> computeSimilarity(
		ArrayList<HashMap<String, String>> originalhashes, 
		ArrayList<HashMap<String, String>> predictedhashes
		) {
        Integer truePositives = 0;
        Integer falsePositives = 0;
        Integer falseNegatives = 0;

        for (HashMap<String, String> hash1 : originalhashes) {
            boolean matchFound = false;
            for (HashMap<String, String> hash2 : predictedhashes) {
				double normalizedHammingDist = normalizedHammingDistance(hash1.get("value"), hash2.get("value"));
                if (normalizedHammingDist < hashingConfiguration.HASHING_THRESHOLD ) {
                    truePositives++;
                    matchFound = true;
					String description = "\npredicted: " + hash2.toString();
                    break;
                }
            }
            if (!matchFound) {
				String description = "original entity not found in predicted.\n ";
                falseNegatives++;
            }
        }
        falsePositives = predictedhashes.size() - truePositives;

		HashMap<String, Integer> confusionMatrix = new HashMap<String, Integer>();
		confusionMatrix.put("tp", truePositives);
		confusionMatrix.put("fp", falsePositives);
		confusionMatrix.put("fn", falseNegatives);

		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("confusionMatrix", confusionMatrix);
        return result;
    }
}