package com.mdre.evaluation.services;

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

import com.mdre.evaluation.ModelComparisonUtils;

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

public class EvaluateClasses extends YAMTLModule {

	public EvaluateClasses() {
		header().in("in", EcorePackage.eINSTANCE);
	}

	// function get counts of each EcorePackage literal
	public HashMap<String, Integer> getCountOfAllLiterals(String modelFilePath) {
		EvaluateClasses countEngine = new EvaluateClasses();
		countEngine.selectedExecutionPhases = ExecutionPhase.HELPER_ONLY;
		countEngine.loadInputModels(Map.of("in", modelFilePath));
		countEngine.execute();

		HashMap<String, Integer> result = new HashMap<String, Integer>();
		result.put("classes", countEngine.allInstances(EcorePackage.eINSTANCE.getEClass()).size());

		// get classes without dependencies
		Integer countOfClassesWithoutDependencies = 0;
		for (Object object: countEngine.allInstances(EcorePackage.eINSTANCE.getEClass()).toArray()) {
			EClass eclass = (EClass) object;
			List<EAnnotation> annotations = eclass.getEAnnotations();
			Boolean dependencyFound = false;
			for (EAnnotation annotation: annotations) {
				if (annotation.getSource().equals("dependency")) {
					dependencyFound = true;
				}
			}
			if (!dependencyFound) {
				countOfClassesWithoutDependencies = countOfClassesWithoutDependencies + 1;
			}
		}
		result.put("classesWithoutDependencies", countOfClassesWithoutDependencies);


		result.put("attributes",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEAttribute()).size());
		result.put("references",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEReference()).size());
		result.put("operations",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEOperation()).size());
		result.put("parameters",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEParameter()).size());
		result.put("enumerations",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEEnum()).size());
		return result;
	}

	// function get counts of each EcorePackage literal
	public HashMap<String, Object[]> getAllLiterals(String modelFilePath) {
		EvaluateClasses countEngine = new EvaluateClasses();
		countEngine.selectedExecutionPhases = ExecutionPhase.HELPER_ONLY;
		countEngine.loadInputModels(Map.of("in", modelFilePath));
		countEngine.execute();
		HashMap<String, Object[]> result = new HashMap<String, Object[]>();
		result.put("classes", countEngine.allInstances(EcorePackage.eINSTANCE.getEClass()).toArray());
		
		// get classes without dependencies
		ArrayList<Object> classesWithoutDependencies = new ArrayList<Object>();
		for (Object object: countEngine.allInstances(EcorePackage.eINSTANCE.getEClass()).toArray()) {
			EClass eclass = (EClass) object;
			List<EAnnotation> annotations = eclass.getEAnnotations();
			Boolean dependencyFound = false;
			for (EAnnotation annotation: annotations) {
				if (annotation.getSource().equals("dependency")) {
					dependencyFound = true;
				}
			}
			if (!dependencyFound) {
				classesWithoutDependencies.add(object);
			}
		}
		
		
		result.put("classesWithoutDependencies", (Object[]) classesWithoutDependencies.toArray());
		
		result.put("attributes",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEAttribute()).toArray());
		result.put("references",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEReference()).toArray());
		result.put("operations",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEOperation()).toArray());
		result.put("parameters",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEParameter()).toArray());
		result.put("enumerations",
				countEngine.allInstances(EcorePackage.eINSTANCE.getEEnum()).toArray());
		return result;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEReferences(List<EReference> eReferencesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EReference eref: eReferencesArray) {
			HashMap<String, String> digest = new HashMap<String, String>();
			digest.put("name", eref.getName().toLowerCase());
			digest.put("lowerBound", Integer.toString(eref.getLowerBound()));
			digest.put("upperBound", Integer.toString(eref.getUpperBound()));
			digest.put("isContainment", Boolean.toString(eref.isContainment()));
			digest.put("containingClass", eref.getEContainingClass().getName().toLowerCase());
			digest.put("order", Boolean.toString(eref.isOrdered()));
			digest.put("unique", Boolean.toString(eref.isUnique()));
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEClasses(List<EClass> eClassesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EClass eClass: eClassesArray) {
			HashMap<String, String> digest = new HashMap<String, String>();
			digest.put("name", eClass.getName().toLowerCase());
			// digest.put("instanceClassName", eClass.getInstanceClassName());
			// digest.put("abstract", Boolean.toString(eClass.isAbstract()));
			// digest.put("interface", Boolean.toString(eClass.isInterface()));
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEnums(Object[] enumsArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(Object object: enumsArray) {
			EEnum enumObject = (EEnum) object; 
			HashMap<String, String> digest = new HashMap<String, String>();
			digest.put("name", enumObject.getName().toLowerCase());
			// digest.put("instanceClassName", eClass.getInstanceClassName());
			// digest.put("abstract", Boolean.toString(eClass.isAbstract()));
			// digest.put("interface", Boolean.toString(eClass.isInterface()));
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEClasses(Object[] eClassesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(Object a: eClassesArray) {
			EClass eref = (EClass) a;
			HashMap<String, String> digest = new HashMap<String, String>();
			digest.put("name", eref.getName().toLowerCase());
			// digest.put("instanceClassName", eref.getInstanceClassName());
			// digest.put("abstract", Boolean.toString(eref.isAbstract()));
			// digest.put("interface", Boolean.toString(eref.isInterface()));
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	public static ArrayList<HashMap<String, String>> getDigestArrayForEAtrributes(List<EAttribute> eAttributesArray) {
		ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
		for(EAttribute eAtt: eAttributesArray) {
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
			arrayOfDigests.add(digest);
		}
		return arrayOfDigests;
	}

	 public static ArrayList<HashMap<String, String>> getDigestArrayForEOperations(List<EOperation> eOperationsArray) {
	 	ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
	 	for(Object a: eOperationsArray) {
	 		EOperation eop = (EOperation) a;
	 		HashMap<String, String> digest = new HashMap<String, String>();
	 		digest.put("name", eop.getName().toLowerCase());
            if (eop.getEContainingClass() != null) {
                digest.put("containerClassName", eop.getEContainingClass().getName().toLowerCase());
            }
            ArrayList<HashMap<String, String>> parametersDigest = getDigestArrayForEParameters(eop.getEParameters());
            Collections.sort(parametersDigest, Comparator.comparing((HashMap<String, String> map) -> map.get("name")));
			digest.put("parametersDigest", parametersDigest.toString());
			arrayOfDigests.add(digest);
	 	}
	 	return arrayOfDigests;
	 }

	 public static ArrayList<HashMap<String, String>> getDigestArrayForEParameters(List<EParameter> eParametersArray) {
	 	ArrayList<HashMap<String, String>> arrayOfDigests = new ArrayList<HashMap<String, String>>();
	 	for(EParameter eparam: eParametersArray) {
	 		HashMap<String, String> digest = new HashMap<String, String>();
	 		digest.put("name", eparam.getName().toLowerCase());
			if (eparam.getEType() != null) {
		 		digest.put("type", eparam.getEType().getName().toLowerCase());
			}
            if (eparam.getEOperation() != null) {
                digest.put("containingMethodName", eparam.getEOperation().getName().toLowerCase());
            }
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

    public static HashMap<String, Float> computeMetrics(HashMap<String, Integer> metrics) {
        Float tp = (float) metrics.getOrDefault("tp", 0);
        Float fp = (float) metrics.getOrDefault("fp", 0);
        Float fn = (float) metrics.getOrDefault("fn", 0);

        Float precision = computePrecision(tp, fp);
        Float recall = computeRecall(tp, fn);
        Float f1Score = computeF1Score(precision, recall);

		HashMap<String, Float> returnValue = new HashMap<String, Float>();
		returnValue.put("precision", precision);
		returnValue.put("recall", recall);
		returnValue.put("f1Score", f1Score);
        return returnValue;
    }

    public static Float computePrecision(Float tp, Float fp) {
        if (tp + fp == 0) {
            return (float) 0;
        }
        return (Float) tp / (tp + fp);
    }

    public static Float computeRecall(float tp, float fn) {
        if (tp + fn == 0) {
            return (float) 0;
        }
        return tp / (tp + fn);
    }

    public static Float computeF1Score(float precision, float recall) {
        if (precision + recall == 0) {
            return (float) 0;
        }
        return 2 * (precision * recall) / (precision + recall);
    }

	public static List<EReference> getRegularReferences(List<EReference> references, List<EReference> containments) {
		ArrayList<EReference> regularReferences = new ArrayList<EReference>();
		for (EReference eref: references) {
			Boolean found = false;
			for (EReference erefContainment: containments) {
				if (ModelComparisonUtils.compareNames(eref.getName(), erefContainment.getName())) {
					found = true;
				}
			}
			if (!found) {
				regularReferences.add(eref);
			}
		}
		return regularReferences;
	}
	
	public static HashMap<String, Object> compareClasses(EClass erefOriginal, EClass erefPredicted) {
		HashMap<String, Object> classLevelMetrics = new HashMap<String, Object>();
		classLevelMetrics.put("class_name_model1", erefOriginal.getName());
		classLevelMetrics.put("class_name_model2", erefPredicted.getName());

		EvaluateClasses countEngine = new EvaluateClasses();
		HashMap<String, Object> attributeResultObject = computeSimilarityForDigests(
			countEngine.getDigestArrayForEAtrributes(erefOriginal.getEAttributes()),
			countEngine.getDigestArrayForEAtrributes(erefPredicted.getEAttributes())
		);
		HashMap<String, Integer> attributeConfusionMatrix = (HashMap<String, Integer>) attributeResultObject.get("confusionMatrix");
		classLevelMetrics.put("class_attributes_model1", erefOriginal.getEAttributes().size());
		classLevelMetrics.put("class_attributes_model2", erefPredicted.getEAttributes().size());
		classLevelMetrics.put("class_attributes_diff_model1_minus_model2", (Integer) classLevelMetrics.get("class_attributes_model1") - (Integer) classLevelMetrics.get("class_attributes_model2"));
		classLevelMetrics.put("class_attributes_diff_model2_minus_model1", (Integer) classLevelMetrics.get("class_attributes_model2") - (Integer) classLevelMetrics.get("class_attributes_model1"));
		classLevelMetrics.put("attributes_tp", attributeConfusionMatrix.get("tp"));
		classLevelMetrics.put("attributes_fp", attributeConfusionMatrix.get("fp"));
		classLevelMetrics.put("attributes_fn", attributeConfusionMatrix.get("fn"));

		HashMap<String, Object> referenceResultObject = computeSimilarityForDigests(
			countEngine.getDigestArrayForEReferences(erefOriginal.getEReferences()),
			countEngine.getDigestArrayForEReferences(erefPredicted.getEReferences())
		);
		HashMap<String, Integer> referenceConfusionMatrix = (HashMap<String, Integer>) referenceResultObject.get("confusionMatrix");
		classLevelMetrics.put("class_references_model1", erefOriginal.getEReferences().size());
		classLevelMetrics.put("class_references_model2", erefPredicted.getEReferences().size());
		classLevelMetrics.put("class_references_diff_model1_minus_model2", (Integer) classLevelMetrics.get("class_references_model1") - (Integer) classLevelMetrics.get("class_references_model2"));
		classLevelMetrics.put("class_references_diff_model2_minus_model1", (Integer) classLevelMetrics.get("class_references_model2") - (Integer) classLevelMetrics.get("class_references_model1"));
		classLevelMetrics.put("references_tp", referenceConfusionMatrix.get("tp"));
		classLevelMetrics.put("references_fp", referenceConfusionMatrix.get("fp"));
		classLevelMetrics.put("references_fn", referenceConfusionMatrix.get("fn"));

		HashMap<String, Object> operationResultObject = computeSimilarityForDigests(
			countEngine.getDigestArrayForEOperations(erefOriginal.getEOperations()),
			countEngine.getDigestArrayForEOperations(erefPredicted.getEOperations())
		);
		HashMap<String, Integer> operationConfusionMatrix = (HashMap<String, Integer>) operationResultObject.get("confusionMatrix");
		classLevelMetrics.put("class_operations_model1", erefOriginal.getEOperations().size());
		classLevelMetrics.put("class_operations_model2", erefPredicted.getEOperations().size());
		classLevelMetrics.put("class_operations_diff_model1_minus_model2", (Integer) classLevelMetrics.get("class_operations_model1") - (Integer) classLevelMetrics.get("class_operations_model2"));
		classLevelMetrics.put("class_operations_diff_model2_minus_model1", (Integer) classLevelMetrics.get("class_operations_model2") - (Integer) classLevelMetrics.get("class_operations_model1"));
		classLevelMetrics.put("operations_tp", operationConfusionMatrix.get("tp"));
		classLevelMetrics.put("operations_fp", operationConfusionMatrix.get("fp"));
		classLevelMetrics.put("operations_fn", operationConfusionMatrix.get("fn"));

		HashMap<String, Object> superTypesResultObject = computeSimilarityForDigests(
			countEngine.getDigestArrayForEClasses(erefOriginal.getESuperTypes()),
			countEngine.getDigestArrayForEClasses(erefPredicted.getESuperTypes())
		);
		HashMap<String, Integer> superTypeConfusionMatrix = (HashMap<String, Integer>) superTypesResultObject.get("confusionMatrix");
		classLevelMetrics.put("class_superTypes_model1", erefOriginal.getESuperTypes().size());
		classLevelMetrics.put("class_superTypes_model2", erefPredicted.getESuperTypes().size());
		classLevelMetrics.put("class_superTypes_diff_model1_minus_model2", (Integer) classLevelMetrics.get("class_superTypes_model1") - (Integer) classLevelMetrics.get("class_superTypes_model2"));
		classLevelMetrics.put("class_superTypes_diff_model2_minus_model1", (Integer) classLevelMetrics.get("class_superTypes_model2") - (Integer) classLevelMetrics.get("class_superTypes_model1"));
		classLevelMetrics.put("superTypes_tp", superTypeConfusionMatrix.get("tp"));
		classLevelMetrics.put("superTypes_fp", superTypeConfusionMatrix.get("fp"));
		classLevelMetrics.put("superTypes_fn", superTypeConfusionMatrix.get("fn"));

		String[] entitiesToCompare = new String[]{
			"attributes",
			"references",
			"operations",
			"superTypes",
		};
		Integer truePositives = 0;
		Integer falsePositives = 0;
		Integer falseNegatives = 0;
		for(String entity: entitiesToCompare) {
			truePositives = truePositives + (Integer) classLevelMetrics.get(entity + "_tp");
			falsePositives = falsePositives + (Integer) classLevelMetrics.get(entity + "_fp");
			falseNegatives = falseNegatives + (Integer) classLevelMetrics.get(entity + "_fn");
		}
		classLevelMetrics.put("aggregate_tp", truePositives);
		classLevelMetrics.put("aggregate_fp", falsePositives);
		classLevelMetrics.put("aggregate_fn", falseNegatives);
		classLevelMetrics.put("aggregate_class_precision", computePrecision((float) truePositives, (float) falsePositives));
		classLevelMetrics.put("aggregate_class_recall", computeRecall(truePositives, falseNegatives));
		classLevelMetrics.put("aggregate_class_f1_score", computeF1Score((float) classLevelMetrics.get("aggregate_class_precision"), (float) classLevelMetrics.get("aggregate_class_recall")));
		classLevelMetrics.put("semantic_similarity_average", -1);

		return classLevelMetrics;
	}

public static HashMap<String, Object> createModelLevelMetricsFromClassLevelMetrics(
        HashMap<String, HashMap<String, Object>> allMatchedClassesMetrics,
        ArrayList<HashMap<String, Integer>> allOriginalClassesMetricsNotMatched,
        ArrayList<HashMap<String, Integer>> allPredictedClassesMetricsNotMatched,
        HashMap<String, Integer> original_literal_counts,
        HashMap<String, Integer> generated_literal_counts,
        String original_model,
        String generated_model
    ) {
        EvaluateClasses countEngine = new EvaluateClasses();

        HashMap<String, Object> modelLevelMetrics = new HashMap<>();

		// classes
        Integer total_classes_model1 = allMatchedClassesMetrics.size() + allOriginalClassesMetricsNotMatched.size();
        Integer total_classes_model2 = allMatchedClassesMetrics.size() + allPredictedClassesMetricsNotMatched.size();
        Integer classes_tp = allMatchedClassesMetrics.size();
        Integer classes_fn = allOriginalClassesMetricsNotMatched.size();
        Integer classes_fp = allPredictedClassesMetricsNotMatched.size();
		modelLevelMetrics.put("total_classes_model1", total_classes_model1);
		modelLevelMetrics.put("total_classes_model2", total_classes_model2);
		modelLevelMetrics.put("total_classes_diff_model1_minus_model2", total_classes_model1 - total_classes_model2);
		modelLevelMetrics.put("total_classes_diff_model2_minus_model1", total_classes_model2 - total_classes_model1);
		modelLevelMetrics.put("classes_tp", classes_tp);
		modelLevelMetrics.put("classes_fp", classes_fp);
		modelLevelMetrics.put("classes_fn", classes_fn);

        // enumerations
        HashMap<String, Object> enumerationResultObject = computeSimilarityForDigests(
            countEngine.getDigestArrayForEnums(countEngine.getAllLiterals(original_model).get("enumerations")),
            countEngine.getDigestArrayForEnums(countEngine.getAllLiterals(generated_model).get("enumerations"))
        );
        HashMap<String, Integer> enumerationConfusionMatrix = (HashMap<String, Integer>) enumerationResultObject.get("confusionMatrix");
        modelLevelMetrics.put("enumerations_tp", enumerationConfusionMatrix.get("tp"));
        modelLevelMetrics.put("enumerations_fp", enumerationConfusionMatrix.get("fp"));
        modelLevelMetrics.put("enumerations_fn", enumerationConfusionMatrix.get("fn"));
		Integer total_enumerations_model1 = original_literal_counts.get("enumerations");
		Integer total_enumerations_model2 = generated_literal_counts.get("enumerations");
		modelLevelMetrics.put("total_enumerations_model1", total_enumerations_model1);
		modelLevelMetrics.put("total_enumerations_model2", total_enumerations_model2);
		modelLevelMetrics.put("total_enumerations_diff_model1_minus_model2", total_enumerations_model1 - total_enumerations_model2);
		modelLevelMetrics.put("total_enumerations_diff_model2_minus_model1", total_enumerations_model2 - total_enumerations_model1);

        String[] metrics = {
            "attributes", "references", "operations", "superTypes"
        };

        for (String metric : metrics) {
            int tp = 0, fn = 0, fp = 0;
            for (Object model : allMatchedClassesMetrics.values()) {
                HashMap<String, Object> modelParsed = (HashMap<String, Object>) model;
                tp += (Integer) modelParsed.get(metric + "_tp");
                fn += (Integer) modelParsed.get(metric + "_fn");
                fp += (Integer) modelParsed.get(metric + "_fp");
            }
            for (Object model : allOriginalClassesMetricsNotMatched) {
                HashMap<String, Object> modelParsed = (HashMap<String, Object>) model;
                fn += (Integer) modelParsed.get(metric);
            }
            for (Object model : allPredictedClassesMetricsNotMatched) {
                HashMap<String, Object> modelParsed = (HashMap<String, Object>) model;
                fp += (Integer) modelParsed.get(metric);
            }
            modelLevelMetrics.put(metric + "_tp", tp);
            modelLevelMetrics.put(metric + "_fn", fn);
            modelLevelMetrics.put(metric + "_fp", fp);
        }

        for (String metric : metrics) {
            int total_model1 = 0, total_model2 = 0;
            for (Object model : allMatchedClassesMetrics.values()) {
                HashMap<String, Object> modelParsed = (HashMap<String, Object>) model;
                total_model1 += (Integer) modelParsed.get("class_" + metric + "_model1");
                total_model2 += (Integer) modelParsed.get("class_" + metric + "_model2");
            }
            for (Object model : allOriginalClassesMetricsNotMatched) {
                HashMap<String, Object> modelParsed = (HashMap<String, Object>) model;
                total_model1 += (Integer) modelParsed.get(metric);
            }
            for (Object model : allPredictedClassesMetricsNotMatched) {
                HashMap<String, Object> modelParsed = (HashMap<String, Object>) model;
                total_model2 += (Integer) modelParsed.get(metric);
            }
            modelLevelMetrics.put("total_" + metric + "_model1", total_model1);
            modelLevelMetrics.put("total_" + metric + "_model2", total_model2);
            modelLevelMetrics.put("total_" + metric + "_diff_model1_minus_model2", total_model1 - total_model2);
            modelLevelMetrics.put("total_" + metric + "_diff_model2_minus_model1", total_model2 - total_model1);
        }

        // Compute aggregate metrics
        String[] allMetrics = {
            "classes", "attributes", "references", "operations", "superTypes", "enumerations"
        };
        int truePositives_aggregate = 0, falsePositives_aggregate = 0, falseNegatives_aggregate = 0;
        for (String metric : allMetrics) {
            truePositives_aggregate += (Integer) modelLevelMetrics.get(metric + "_tp");
            falsePositives_aggregate += (Integer) modelLevelMetrics.get(metric + "_fp");
            falseNegatives_aggregate += (Integer) modelLevelMetrics.get(metric + "_fn");
        }
        modelLevelMetrics.put("aggregate_tp", truePositives_aggregate);
        modelLevelMetrics.put("aggregate_fp", falsePositives_aggregate);
        modelLevelMetrics.put("aggregate_fn", falseNegatives_aggregate);
        modelLevelMetrics.put("aggregate_model_precision", computePrecision((float) truePositives_aggregate, (float) falsePositives_aggregate));
        modelLevelMetrics.put("aggregate_model_recall", computeRecall(truePositives_aggregate, falseNegatives_aggregate));
        modelLevelMetrics.put("aggregate_model_f1_score", computeF1Score((float) modelLevelMetrics.get("aggregate_model_precision"), (float) modelLevelMetrics.get("aggregate_model_recall")));
        modelLevelMetrics.put("semantic_similarity_average", -1);

        return modelLevelMetrics;
    }

	/*
		Parameters: 
			* models: ArrayList<HashMap<String, String>>. Example: [{"original: <ecore model file path>, "generated": <ecore model file path>}]
			* includeDependencies: Boolean.
	*/
	public static HashMap<String, JSONObject> compareModels(ArrayList<HashMap<String, String>> models, Boolean includeDependencies) {
		// initialize class level analysis object for matched classes
		HashMap<String, Object> allMatchedClassesMetrics = new HashMap<String, Object>();

		// initialize model level analysis object
		HashMap<String, Object> modelLevelResultObject = new HashMap<String, Object>();

		// perform class level comparison
		for(HashMap<String, String> model: models) {
			EvaluateClasses countEngine = new EvaluateClasses();
			String original_model = model.get("original");
			String generated_model = model.get("generated");
			HashMap<String, Integer> original_literal_counts = countEngine.getCountOfAllLiterals(original_model);
			HashMap<String, Integer> generated_literal_counts = countEngine.getCountOfAllLiterals(generated_model);
			Object[] allClassLiteralsForOriginalModel = countEngine.getAllLiterals(original_model).get("classes");
			Object[] allClassLiteralsForGeneratedlModel;
			if (includeDependencies) {
				allClassLiteralsForGeneratedlModel = countEngine.getAllLiterals(generated_model).get("classes");
			} else {
				allClassLiteralsForGeneratedlModel = countEngine.getAllLiterals(generated_model).get("classesWithoutDependencies");
			}

			// initialize class level analysis object
			HashMap<String, HashMap<String, Object>> matchedClassMetrics = new HashMap<String, HashMap<String, Object>>();

			// initialize class level analysis object for original classes not matched
			ArrayList<HashMap<String, Integer>> allOriginalClassesMetricsNotMatched = new ArrayList<HashMap<String, Integer>>();

			// initialize class level analysis object for predicted classes not matched
			ArrayList<HashMap<String, Integer>> allPredictedClassesMetricsNotMatched = new ArrayList<HashMap<String, Integer>>();
			
			// find a matching class for each original class
			for(Object classLiteral: allClassLiteralsForOriginalModel) {
				EClass erefOriginal = (EClass) classLiteral;
				Boolean matched = false;
				for (Object classLiteralPredicted: allClassLiteralsForGeneratedlModel) {
					EClass erefPredicted = (EClass) classLiteralPredicted;
					if (ModelComparisonUtils.compareNames(erefOriginal.getName(), erefPredicted.getName())) {
						matched = true;
						HashMap<String, Object> classLevelMetrics = compareClasses(erefOriginal, erefPredicted);
						matchedClassMetrics.put(erefOriginal.getName(), classLevelMetrics);
					}
				}
				if (!matched) {
					HashMap<String, Integer> metricsNotMatched = new HashMap<String, Integer>();
					metricsNotMatched.put("attributes", erefOriginal.getEAttributes().size());
					metricsNotMatched.put("references", erefOriginal.getEReferences().size());
					metricsNotMatched.put("operations", erefOriginal.getEOperations().size());
					metricsNotMatched.put("superTypes", erefOriginal.getESuperTypes().size());
					allOriginalClassesMetricsNotMatched.add(metricsNotMatched);
				}
			}

			// count predicted classes not matched along with their elements
			for (Object classLiteralPredicted: allClassLiteralsForGeneratedlModel) {
				EClass erefPredicted = (EClass) classLiteralPredicted;
				Boolean matched = false;
				for (Object classLiteral: allClassLiteralsForOriginalModel) {
					EClass erefOriginal = (EClass) classLiteral;
					if (ModelComparisonUtils.compareNames(erefPredicted.getName(), erefOriginal.getName())) {
						matched = true;
					}
				}
				if (!matched) {
					HashMap<String, Integer> metricsNotMatched = new HashMap<String, Integer>();
					metricsNotMatched.put("attributes", erefPredicted.getEAttributes().size());
					metricsNotMatched.put("references", erefPredicted.getEReferences().size());
					metricsNotMatched.put("operations", erefPredicted.getEOperations().size());
					metricsNotMatched.put("superTypes", erefPredicted.getESuperTypes().size());
					allPredictedClassesMetricsNotMatched.add(metricsNotMatched);
				}
			}

			// Create model level metrics object informed from class level analysis
			HashMap<String, Object> modelLevelMetrics = createModelLevelMetricsFromClassLevelMetrics(
				matchedClassMetrics, 
				allOriginalClassesMetricsNotMatched,
				allPredictedClassesMetricsNotMatched,
				original_literal_counts,
				generated_literal_counts,
				original_model,
				generated_model
			);
			modelLevelMetrics.put("model1_identifier", original_model);
			modelLevelMetrics.put("model2_identifier", generated_model);
			modelLevelResultObject.put(model.get("projectName"), modelLevelMetrics);
			allMatchedClassesMetrics.put(model.get("projectName"), matchedClassMetrics);
		}
		// write json to file
		JSONObject jsonResultsClass = new JSONObject(allMatchedClassesMetrics);
		JSONObject jsonResultsModel = new JSONObject(modelLevelResultObject);
		HashMap<String, JSONObject> results = new HashMap<String, JSONObject>();
		results.put("classLevelJson", jsonResultsClass);
		results.put("modelLevelJson", jsonResultsModel);
		return results;
	}

	private static void println(Object text) {
		System.out.println(text);
	}

}
