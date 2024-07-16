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

public class HashingService {

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

	public static String getHashForEReference(EReference eref) {
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

	public static String getHashForEClass(EClass eClass) {
        long totalChecksum = 0;
        totalChecksum += computeCRC32(eClass.getName().toLowerCase());
        String binaryHash = Long.toBinaryString(totalChecksum);
        return binaryHash;
	}

	public static String getHashForEnum(EEnum enumeration) {
        long totalChecksum = 0;
        totalChecksum += computeCRC32(enumeration.getName().toLowerCase());
        String binaryHash = Long.toBinaryString(totalChecksum);
        return binaryHash;
	}

	public static String getHashForEAttribute(EAttribute eAtt) {
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

	public static String getHashForEoperation(EOperation eop) {
        long totalChecksum = 0;
        totalChecksum += computeCRC32(eop.getName().toLowerCase());
		if (eop.getEContainingClass() != null) {
	        totalChecksum += computeCRC32(eop.getEContainingClass().getName().toLowerCase());
		}
		// ArrayList<String> parametershash = getHashArrayForEParameters(eop.getEParameters());
		// for (String hash : parametershash) {
        //     long numericHash = Long.parseLong(hash);;
        //     totalChecksum += numericHash;
        // }
        String binaryHash = Long.toBinaryString(totalChecksum);
		return binaryHash;
	}

	public static String getHashForEParameter(EParameter eparam) {
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

	public static ArrayList<String> getHashArrayForEReferences(List<EReference> eReferencesArray) {
		ArrayList<String> arrayOfHashes = new ArrayList<String>();
		for(EReference eref: eReferencesArray) {
			String hash = getHashForEReference(eref);
			arrayOfHashes.add(hash);
		}
		return arrayOfHashes;
	}

	public static ArrayList<String> getHashArrayForEClasses(List<EClass> eClassesArray) {
		ArrayList<String> arrayOfHashes = new ArrayList<String>();
		for(EClass eclass: eClassesArray) {
			String hash = getHashForEClass(eclass);
			arrayOfHashes.add(hash);
		}
		return arrayOfHashes;
	}

	public static ArrayList<String> getHashArrayForEnums(Object[] enumsArray) {
		ArrayList<String> arrayOfHashes = new ArrayList<String>();
		for(Object object: enumsArray) {
			EEnum enumObject = (EEnum) object; 
			String hash = getHashForEnum(enumObject);
			arrayOfHashes.add(hash);
		}
		return arrayOfHashes;
	}

	public static ArrayList<String> getHashArrayForEClasses(Object[] eClassesArray) {
		ArrayList<String> arrayOfHashes = new ArrayList<String>();
		for(Object a: eClassesArray) {
			EClass eclass = (EClass) a;
			String hash = getHashForEClass(eclass);
			arrayOfHashes.add(hash);
		}
		return arrayOfHashes;
	}

	public static ArrayList<String> getHashArrayForEAtrributes(List<EAttribute> eAttributesArray) {
		ArrayList<String> arrayOfHashes = new ArrayList<String>();
		for(EAttribute eAtt: eAttributesArray) {
			String hash = getHashForEAttribute(eAtt);
			arrayOfHashes.add(hash);
		}
		return arrayOfHashes;
	}

	 public static ArrayList<String> getHashArrayForEOperations(List<EOperation> eOperationsArray) {
	 	ArrayList<String> arrayOfHashes = new ArrayList<String>();
	 	for(Object a: eOperationsArray) {
	 		EOperation eop = (EOperation) a;
	 		String hash = getHashForEoperation(eop);
			arrayOfHashes.add(hash);
	 	}
	 	return arrayOfHashes;
	 }

	 public static ArrayList<String> getHashArrayForEParameters(List<EParameter> eParametersArray) {
	 	ArrayList<String> arrayOfHashes = new ArrayList<String>();
	 	for(EParameter eparam: eParametersArray) {
	 		String hash = getHashForEParameter(eparam);
	 		arrayOfHashes.add(hash);
	 	}
	 	return arrayOfHashes;
	 }	

    public static HashMap<String, Object> computeSimilarityForHashes(
		ArrayList<String> originalhashes, 
		ArrayList<String> predictedhashes
		) {
        Integer truePositives = 0;
        Integer falsePositives = 0;
        Integer falseNegatives = 0;
		double threshold = 0.7;

        for (String hash1 : originalhashes) {
            boolean matchFound = false;
            for (String hash2 : predictedhashes) {
				double normalizedHammingDist = normalizedHammingDistance(hash1, hash2);
                if (normalizedHammingDist < threshold ) {
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