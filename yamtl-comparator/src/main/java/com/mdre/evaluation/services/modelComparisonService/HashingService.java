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

public class HashingService extends AbstractClassComparisonService {
	private double HASHING_THRESHOLD;
	public HashingService(JSONObject configuration) {
		// configuration
		HASHING_THRESHOLD = configuration.getDouble("HASHING_THRESHOLD");
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
        totalChecksum += computeCRC32(eref.getName().toLowerCase());
        totalChecksum += computeCRC32(Integer.toString(eref.getLowerBound()));
        totalChecksum += computeCRC32(Integer.toString(eref.getUpperBound()));
        totalChecksum += computeCRC32(Boolean.toString(eref.isContainment()));
        totalChecksum += computeCRC32(eref.getEContainingClass().getName().toLowerCase());
        totalChecksum += computeCRC32(Boolean.toString(eref.isOrdered()));
        totalChecksum += computeCRC32(Boolean.toString(eref.isUnique()));
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
        totalChecksum += computeCRC32(enumeration.getName().toLowerCase());
        String binaryHash = Long.toBinaryString(totalChecksum);
        return binaryHash;
	}

	public String getComparableObjectForEAttribute(EAttribute eAtt) {
        long totalChecksum = 0;
        totalChecksum += computeCRC32(eAtt.getName().toLowerCase());
		if (eAtt.getEContainingClass() != null) {
	        totalChecksum += computeCRC32(eAtt.getEContainingClass().getName().toLowerCase());
		}
		if (eAtt.getEAttributeType() != null) {
	        totalChecksum += computeCRC32(eAtt.getEAttributeType().getName().toLowerCase());
		}
        totalChecksum += computeCRC32(Integer.toString(eAtt.getLowerBound()));
        totalChecksum += computeCRC32(Integer.toString(eAtt.getUpperBound()));
        totalChecksum += computeCRC32(Boolean.toString(eAtt.isOrdered()));
        totalChecksum += computeCRC32(Boolean.toString(eAtt.isUnique()));
        String binaryHash = Long.toBinaryString(totalChecksum);
		return binaryHash;
	}

	public String getComparableObjectForEoperation(EOperation eop) {
        long totalChecksum = 0;
        totalChecksum += computeCRC32(eop.getName().toLowerCase());
		if (eop.getEContainingClass() != null) {
	        totalChecksum += computeCRC32(eop.getEContainingClass().getName().toLowerCase());
		}
		// ArrayList<HashMap<String, String>> parametershash = getComparableObjectArrayForEParameters(eop.getEParameters());
		// for (String hash : parametershash) {
        //     long numericHash = Long.parseLonghash);;
        //     totalChecksum += numericHash;
        // }
        String binaryHash = Long.toBinaryString(totalChecksum);
		return binaryHash;
	}

	public String getComparableObjectForEParameter(EParameter eparam) {
        long totalChecksum = 0;
        totalChecksum += computeCRC32(eparam.getName().toLowerCase());
        totalChecksum += computeCRC32(eparam.getName().toLowerCase());
		if (eparam.getEType() != null) {
	        totalChecksum += computeCRC32(eparam.getEType().getName().toLowerCase());
		}
		if (eparam.getEOperation() != null) {
	        totalChecksum += computeCRC32(eparam.getEOperation().getName().toLowerCase());
		}		
        String binaryHash = Long.toBinaryString(totalChecksum);
		return binaryHash;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEReferences(List<EReference> eReferencesArray) {
		ArrayList<HashMap<String, String>> arrayOfHashes = new ArrayList<HashMap<String, String>>();
		for(EReference eref: eReferencesArray) {
			String hash = getComparableObjectForEReference(eref);
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("value", hash);
			arrayOfHashes.add(hashValue);
		}
		return arrayOfHashes;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEClasses(List<EClass> eClassesArray) {
		ArrayList<HashMap<String, String>> arrayOfHashes = new ArrayList<HashMap<String, String>>();
		for(EClass eclass: eClassesArray) {
			String hash = getComparableObjectForEClass(eclass);
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("value", hash);
			arrayOfHashes.add(hashValue);
		}
		return arrayOfHashes;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEnums(Object[] enumsArray) {
		ArrayList<HashMap<String, String>> arrayOfHashes = new ArrayList<HashMap<String, String>>();
		for(Object object: enumsArray) {
			EEnum enumObject = (EEnum) object; 
			String hash = getComparableObjectForEnum(enumObject);
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("value", hash);
			arrayOfHashes.add(hashValue);
		}
		return arrayOfHashes;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEClasses(Object[] eClassesArray) {
		ArrayList<HashMap<String, String>> arrayOfHashes = new ArrayList<HashMap<String, String>>();
		for(Object a: eClassesArray) {
			EClass eclass = (EClass) a;
			String hash = getComparableObjectForEClass(eclass);
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("value", hash);
			arrayOfHashes.add(hashValue);
		}
		return arrayOfHashes;
	}

	public ArrayList<HashMap<String, String>> getComparableObjectArrayForEAtrributes(List<EAttribute> eAttributesArray) {
		ArrayList<HashMap<String, String>> arrayOfHashes = new ArrayList<HashMap<String, String>>();
		for(EAttribute eAtt: eAttributesArray) {
			String hash = getComparableObjectForEAttribute(eAtt);
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("value", hash);
			arrayOfHashes.add(hashValue);
		}
		return arrayOfHashes;
	}

	 public ArrayList<HashMap<String, String>> getComparableObjectArrayForEOperations(List<EOperation> eOperationsArray) {
	 	ArrayList<HashMap<String, String>> arrayOfHashes = new ArrayList<HashMap<String, String>>();
	 	for(Object a: eOperationsArray) {
	 		EOperation eop = (EOperation) a;
	 		String hash = getComparableObjectForEoperation(eop);
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("value", hash);
			arrayOfHashes.add(hashValue);
	 	}
	 	return arrayOfHashes;
	 }

	 public ArrayList<HashMap<String, String>> getComparableObjectArrayForEParameters(List<EParameter> eParametersArray) {
	 	ArrayList<HashMap<String, String>> arrayOfHashes = new ArrayList<HashMap<String, String>>();
	 	for(EParameter eparam: eParametersArray) {
	 		String hash = getComparableObjectForEParameter(eparam);
	 		HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("value", hash);
			arrayOfHashes.add(hashValue);
	 	}
	 	return arrayOfHashes;
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
                if (normalizedHammingDist < HASHING_THRESHOLD ) {
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