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

import com.mdre.evaluation.utils.Tuple;
import com.mdre.evaluation.services.modelComparisonService.AbstractClassComparisonService;
import com.mdre.evaluation.config.Constants;
import com.mdre.evaluation.dtos.HashingConfigurationDTO;
import com.mdre.evaluation.dtos.VenDiagramDTO;
import com.mdre.evaluation.dtos.MatchedElementsDTO;

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

	public double computeSimilarity(Object comparisonObject1, Object comparisonObject2) {
		String hashA = (String) comparisonObject1;
		String hashB = (String) comparisonObject2;
        if (hashA.length() != hashB.length()) {
            throw new IllegalArgumentException("Hashes must be of the same length");
        }

        int length = hashA.length();
        int dotProduct = 0;
        int normA = 0;
        int normB = 0;

        for (int i = 0; i < length; i++) {
            int bitA = Character.getNumericValue(hashA.charAt(i));
            int bitB = Character.getNumericValue(hashB.charAt(i));

            dotProduct += bitA * bitB;
            normA += bitA * bitA;
            normB += bitB * bitB;
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
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

	public static String getComparableObjectForEClass(Object obj) {
		EClass eclass = (EClass) obj;
        long totalChecksum = 0;
        totalChecksum += computeCRC32(eclass.getName().toLowerCase());
        String binaryHash = String.format("%32s", Long.toBinaryString(totalChecksum)).replace(' ', '0');
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

	@FunctionalInterface
	interface ComparisonFunction {
		String apply(Object t);
	}

	public <T> VenDiagramDTO<T> computeIntersection(List<T> model1elements, List<T> model2elements, ComparisonFunction processor) {
		VenDiagramDTO<T> result = new VenDiagramDTO<T>();
		ArrayList<MatchedElementsDTO<T>> intersection = new ArrayList<MatchedElementsDTO<T>>();
		ArrayList<T> onlyInModel1 = new ArrayList<T>();
		ArrayList<T> onlyInModel2 = new ArrayList<T>();

		HashMap<String, T> hashIndexModel1 = new HashMap<String, T>();
		HashMap<String, T> hashIndexModel2 = new HashMap<String, T>();

		ArrayList<String> model1ElementsHashes = new ArrayList<String>();
		for(T obj: model1elements) {
			String hash = (String) processor.apply(obj);
			model1ElementsHashes.add(hash);
			hashIndexModel1.put(hash, obj);
		}

		ArrayList<String> model2ElementsHashes = new ArrayList<String>();
		for(T obj: model2elements) {
			String hash = (String) processor.apply(obj);
			model2ElementsHashes.add(hash);
			hashIndexModel2.put(hash, obj);
		}

        for (String hash1 : model1ElementsHashes) {
			double maxSimilarity = -1.0;
			Tuple<String, String> candidateToPop = new Tuple<String, String>(null, null);
            for (String hash2 : model2ElementsHashes) {
				Tuple<String, String> matchedCandidate = new Tuple<String, String>(hash1, hash2);
				double similarity = computeSimilarity(hash1, hash2);
				if (similarity > maxSimilarity) {
					maxSimilarity = similarity;
					candidateToPop = matchedCandidate;
				}
            }
			if (maxSimilarity > hashingConfiguration.HASHING_THRESHOLD) { // match found
				model2ElementsHashes.remove(candidateToPop.second);
				MatchedElementsDTO<T> matchedElements = new MatchedElementsDTO<T>();
				matchedElements.model1 = hashIndexModel1.get(candidateToPop.first);
				matchedElements.model2 = hashIndexModel2.get(candidateToPop.second);
				intersection.add(matchedElements);
			} else {
				onlyInModel1.add(hashIndexModel1.get(hash1));
			}
        }

		for (String hash2: model2ElementsHashes) {
			onlyInModel2.add(hashIndexModel2.get(hash2));
		}

		result.matched = intersection;
		result.onlyInModel1 = onlyInModel1;
		result.onlyInModel2 = onlyInModel2;
		return result;		
	}

	public VenDiagramDTO<EClass> getVenDiagramForClasses(List<EClass> classesModel1, List<EClass> classesModel2) {
		VenDiagramDTO<EClass> result = computeIntersection(classesModel1, classesModel2, (eclass) -> getComparableObjectForEClass(eclass));
		return result;
	}

	public VenDiagramDTO<EEnum> getVenDiagramForEnumerations(List<EEnum> enumsModel1, List<EEnum> enumsModel2) {
		VenDiagramDTO<EEnum> result = new VenDiagramDTO<EEnum>();
		ArrayList<MatchedElementsDTO<EEnum>> intersection = new ArrayList<MatchedElementsDTO<EEnum>>();
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
					MatchedElementsDTO<EEnum> matchedEnums = new MatchedElementsDTO<EEnum>();
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

	public VenDiagramDTO<EAttribute> getVenDiagramForEAttributes(List<EAttribute> attributesClass1, List<EAttribute> attributesClass2) {
		VenDiagramDTO<EAttribute> result = new VenDiagramDTO<EAttribute>();
		ArrayList<MatchedElementsDTO<EAttribute>> intersection = new ArrayList<MatchedElementsDTO<EAttribute>>();
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
					MatchedElementsDTO<EAttribute> matchedAttributes = new MatchedElementsDTO<EAttribute>();
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

	public VenDiagramDTO<EReference> getVenDiagramForEReferences(List<EReference> referencesClass1, List<EReference> referencesClass2) {
		VenDiagramDTO<EReference> result = new VenDiagramDTO<EReference>();
		ArrayList<MatchedElementsDTO<EReference>> intersection = new ArrayList<MatchedElementsDTO<EReference>>();
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
					MatchedElementsDTO<EReference> matchedReferences = new MatchedElementsDTO<EReference>();
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

	public VenDiagramDTO<EOperation> getVenDiagramForEOperations(List<EOperation> eoperationsClass1, List<EOperation> eoperationsClass2) {
		VenDiagramDTO<EOperation> result = new VenDiagramDTO<EOperation>();
		ArrayList<MatchedElementsDTO<EOperation>> intersection = new ArrayList<MatchedElementsDTO<EOperation>>();
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
					MatchedElementsDTO<EOperation> matchedElements = new MatchedElementsDTO<EOperation>();
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
}