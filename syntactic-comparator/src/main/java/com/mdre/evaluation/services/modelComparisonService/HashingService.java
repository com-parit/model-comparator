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
	private static HashMap<String, Long> cache;

	public HashingService(HashingConfigurationDTO configuration) {
		this.hashingConfiguration = configuration;
		this.cache = new HashMap<String, Long>();
	}

    public static long computeHashCode(String input) {
		String cHash = computeCRC32(input);
		// check cache
		if (cache.containsKey(cHash)) {
			return cache.get(cHash);
		}
		long hashValue = input.hashCode();
		cache.put(cHash, hashValue);
		return hashValue;
	}

	public static long hashNgram(String input) {
		String cHash = computeCRC32(input);
		// check cache
		if (cache.containsKey(cHash)) {
			return cache.get(cHash);
		}
        long hashValue=0;
		if (input.length() == 1) {
			hashValue = input.hashCode();
			cache.put(cHash, hashValue);
			return hashValue;
		}
		for (int i=0; i < input.length() - 1; i++) {
            String bigram= input.substring(i, i + 2);
            hashValue += (long) bigram.hashCode();
        }
		cache.put(cHash, hashValue);
		return hashValue;
	}

    public static String computeCRC32(String input) {
		CRC32 crc32 = new CRC32();
		crc32.update(input.getBytes());
		long value = crc32.getValue();
		return String.format("%64s", Long.toBinaryString(value)).replace(' ', '0');
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

		double result = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return result;
	}


	public String getComparableObjectForEReference(Object obj) {
		EReference eref = (EReference) obj;
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_REFERENCES_NAME) {
	        totalChecksum += hashNgram(eref.getName() != null ? eref.getName().toLowerCase() : "");
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_LOWER_BOUND) {
	        totalChecksum += computeHashCode(Integer.toString(eref.getLowerBound()));
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_UPPER_BOUND) {
	        totalChecksum += computeHashCode(Integer.toString(eref.getUpperBound()));
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_IS_CONTAINMENT) {
	        totalChecksum += computeHashCode(Boolean.toString(eref.isContainment()));
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_CONTAINING_CLASS) {
	        totalChecksum += hashNgram(eref.getEContainingClass().getName() != null ? eref.getEContainingClass().getName().toLowerCase() : "");
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_IS_ORDERED) {
	        totalChecksum += computeHashCode(Boolean.toString(eref.isOrdered()));
		}
		if (hashingConfiguration.INCLUDE_REFERENCES_IS_UNIQUE) {
	        totalChecksum += computeHashCode(Boolean.toString(eref.isUnique()));
		}
        String binaryHash = String.format("%64s", Long.toBinaryString(totalChecksum)).replace(' ', '0');
        return binaryHash;
	}

	public static String getComparableObjectForEClass(Object obj) {
		EClass eclass = (EClass) obj;
        long totalChecksum = 0;
        totalChecksum += hashNgram(eclass.getName() != null ? eclass.getName().toLowerCase() : "");
        String binaryHash = String.format("%64s", Long.toBinaryString(totalChecksum)).replace(' ', '0');
        return binaryHash;
	}

	public String getComparableObjectForEnum(Object obj) {
		EEnum enumeration = (EEnum) obj;
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_ENUM_NAME) {
	        totalChecksum += hashNgram(enumeration.getName() != null ? enumeration.getName().toLowerCase() : "");
		}
        String binaryHash = String.format("%64s", Long.toBinaryString(totalChecksum)).replace(' ', '0');
        return binaryHash;
	}

	public String getComparableObjectForEAttribute(Object obj) {
		EAttribute eAtt = (EAttribute) obj;
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_NAME) {
	        totalChecksum += hashNgram(eAtt.getName() != null ? eAtt.getName().toLowerCase() : "");
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_CONTAINING_CLASS) {
			if (eAtt.getEContainingClass() != null) {
				totalChecksum += hashNgram(eAtt.getEContainingClass().getName() != null ? eAtt.getEContainingClass().getName().toLowerCase() : "");
			}
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_TYPE) {
			if (eAtt.getEAttributeType() != null) {
				totalChecksum += hashNgram(eAtt.getEAttributeType().getName() != null ? eAtt.getEAttributeType().getName().toLowerCase() : "");
			}
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_LOWER_BOUND) {
	        totalChecksum += computeHashCode(Integer.toString(eAtt.getLowerBound()));
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_UPPER_BOUND) {
	        totalChecksum += computeHashCode(Integer.toString(eAtt.getUpperBound()));
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_IS_ORDERED) {
	        totalChecksum += computeHashCode(Boolean.toString(eAtt.isOrdered()));
		}
		if (hashingConfiguration.INCLUDE_ATTRIBUTE_IS_UNIQUE) {
	        totalChecksum += computeHashCode(Boolean.toString(eAtt.isUnique()));
		}
        String binaryHash = String.format("%64s", Long.toBinaryString(totalChecksum)).replace(' ', '0');
		return binaryHash;
	}

	public String getComparableObjectForEOperation(Object obj) {
		EOperation eop = (EOperation) obj;
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_OPERATION_NAME) {
	        totalChecksum += hashNgram(eop.getName() != null ? eop.getName().toLowerCase() : "");
		}
		if (hashingConfiguration.INCLUDE_OPERATION_CONTAINING_CLASS) {
			if (eop.getEContainingClass() != null) {
				totalChecksum += hashNgram(eop.getEContainingClass().getName() != null ? eop.getEContainingClass().getName().toLowerCase() : "");
			}
		}
		if (hashingConfiguration.INCLUDE_OPERATION_PARAMETERS) {
			for(EParameter eparam: eop.getEParameters()) {
				long hash = getHashForOperationParameters(eparam);
			    totalChecksum += hash;
			}
		}
        String binaryHash = String.format("%64s", Long.toBinaryString(totalChecksum)).replace(' ', '0');
		return binaryHash;
	}

	public String getComparableObjectForEParameter(Object obj) {
		EParameter eparam = (EParameter) obj;
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_PARAMETER_NAME) {
	        totalChecksum += hashNgram(eparam.getName() != null ? eparam.getName().toLowerCase() : "");
		}
		if (hashingConfiguration.INCLUDE_PARAMETER_TYPE) {
			if (eparam.getEType() != null) {
				totalChecksum += hashNgram(eparam.getEType().getName() != null ? eparam.getEType().getName().toLowerCase() : "");
			}
		}
		if (hashingConfiguration.INCLUDE_PARAMETER_OPERATION_NAME) {
			if (eparam.getEOperation() != null) {
				totalChecksum += hashNgram(eparam.getEOperation().getName() != null ? eparam.getEOperation().getName().toLowerCase() : "");
			}
		}
        String binaryHash = String.format("%64s", Long.toBinaryString(totalChecksum)).replace(' ', '0');
		return binaryHash;
	}

	public long getHashForOperationParameters(Object obj) {
		EParameter eparam = (EParameter) obj;
        long totalChecksum = 0;
		if (hashingConfiguration.INCLUDE_PARAMETER_NAME) {
	        totalChecksum += hashNgram(eparam.getName() != null ? eparam.getName().toLowerCase() : "");
		}
		if (hashingConfiguration.INCLUDE_PARAMETER_TYPE) {
			if (eparam.getEType() != null) {
				totalChecksum += hashNgram(eparam.getEType().getName() != null ? eparam.getEType().getName().toLowerCase() : "");
			}
		}
		if (hashingConfiguration.INCLUDE_PARAMETER_OPERATION_NAME) {
			if (eparam.getEOperation() != null) {
				totalChecksum += hashNgram(eparam.getEOperation().getName() != null ? eparam.getEOperation().getName().toLowerCase() : "");
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
			if (maxSimilarity >= hashingConfiguration.HASHING_THRESHOLD) { // match found
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