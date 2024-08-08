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
import org.json.JSONObject;

import com.mdre.evaluation.utils.ModelComparisonUtils;
import com.mdre.evaluation.services.modelComparisonService.HashingService;
import com.mdre.evaluation.services.modelComparisonService.DigestService;
import com.mdre.evaluation.services.modelComparisonService.MetricsComputationService;
import com.mdre.evaluation.services.modelComparisonService.YamtlService;
import com.mdre.evaluation.config.Constants;

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
import com.mdre.evaluation.dtos.ModelComparisonConfigurationDTO;
import com.mdre.evaluation.dtos.HashingConfigurationDTO;
import com.mdre.evaluation.dtos.DigestConfigurationDTO;
import com.mdre.evaluation.dtos.VenDiagramDTO;
import com.mdre.evaluation.dtos.MatchedElementsDTO;

public class ModelComparisonService {

	AbstractClassComparisonService comparisonService;
	ModelComparisonConfigurationDTO modelComparisonConfiguration;
	ArrayList<String> includedElements = new ArrayList<String>();
	ArrayList<String> elementsIncludedInScoring = new ArrayList<String>();

	public ModelComparisonService(ModelComparisonConfigurationDTO configuration) {
		this.modelComparisonConfiguration = configuration;
		if (modelComparisonConfiguration.USE_HASHING) {
			HashingConfigurationDTO hashingConfiguration = modelComparisonConfiguration.hashingConfiguration;
			comparisonService = new HashingService(hashingConfiguration);
		} else {
			DigestConfigurationDTO digestConfiguration = modelComparisonConfiguration.digestConfiguration;
			comparisonService = new DigestService(digestConfiguration);
		}
		if (modelComparisonConfiguration.INCLUDE_CLASS_ATTRIBUTES) {
			includedElements.add(Constants.ATTRIBUTES_IDENTIFIER);
			elementsIncludedInScoring.add(Constants.ATTRIBUTES_IDENTIFIER);
		}
		if (modelComparisonConfiguration.INCLUDE_CLASS_OPERATIONS) {
			includedElements.add(Constants.OPERATIONS_IDENTIFIER);
			elementsIncludedInScoring.add(Constants.OPERATIONS_IDENTIFIER);
		}
		if (modelComparisonConfiguration.INCLUDE_CLASS_REFERENCES) {
			includedElements.add(Constants.REFERENCES_IDENTIFIER);
			elementsIncludedInScoring.add(Constants.REFERENCES_IDENTIFIER);
		}
		if (modelComparisonConfiguration.INCLUDE_CLASS_SUPERTYPES) {
			includedElements.add(Constants.SUPERTYPES_IDENTIFIER);
			elementsIncludedInScoring.add(Constants.SUPERTYPES_IDENTIFIER);
		}
		if (modelComparisonConfiguration.INCLUDE_ENUMS) {
			elementsIncludedInScoring.add(Constants.ENUMS_IDENTIFIER);
		}
		elementsIncludedInScoring.add(Constants.CLASSES_IDENTIFIER);
	}

	public HashMap<String, Object> getClassLevelMetrics(EClass erefOriginal, EClass erefPredicted) {
		HashMap<String, Object> classLevelMetrics = new HashMap<String, Object>();
		classLevelMetrics.put("class_name_model1", erefOriginal.getName());
		classLevelMetrics.put("class_name_model2", erefPredicted.getName());

		VenDiagramDTO<EAttribute> attributeResultObject = comparisonService.getVenDiagramForEAttributes(erefOriginal.getEAttributes(), erefPredicted.getEAttributes());
		classLevelMetrics.put("class_attributes_model1", erefOriginal.getEAttributes().size());
		classLevelMetrics.put("class_attributes_model2", erefPredicted.getEAttributes().size());
		classLevelMetrics.put("class_attributes_diff_model1_minus_model2", (Integer) classLevelMetrics.get("class_attributes_model1") - (Integer) classLevelMetrics.get("class_attributes_model2"));
		classLevelMetrics.put("class_attributes_diff_model2_minus_model1", (Integer) classLevelMetrics.get("class_attributes_model2") - (Integer) classLevelMetrics.get("class_attributes_model1"));
		classLevelMetrics.put("attributes_tp", attributeResultObject.matched.size());
		classLevelMetrics.put("attributes_fp", attributeResultObject.onlyInModel2.size());
		classLevelMetrics.put("attributes_fn", attributeResultObject.onlyInModel1.size());

		VenDiagramDTO<EReference> referenceResultObject = comparisonService.getVenDiagramForEReferences(erefOriginal.getEReferences(), erefPredicted.getEReferences());
		classLevelMetrics.put("class_references_model1", erefOriginal.getEReferences().size());
		classLevelMetrics.put("class_references_model2", erefPredicted.getEReferences().size());
		classLevelMetrics.put("class_references_diff_model1_minus_model2", (Integer) classLevelMetrics.get("class_references_model1") - (Integer) classLevelMetrics.get("class_references_model2"));
		classLevelMetrics.put("class_references_diff_model2_minus_model1", (Integer) classLevelMetrics.get("class_references_model2") - (Integer) classLevelMetrics.get("class_references_model1"));
		classLevelMetrics.put("references_tp", referenceResultObject.matched.size());
		classLevelMetrics.put("references_fp", referenceResultObject.onlyInModel2.size());
		classLevelMetrics.put("references_fn", referenceResultObject.onlyInModel1.size());

		VenDiagramDTO<EOperation> operationResultObject = comparisonService.getVenDiagramForEOperations(erefOriginal.getEOperations(), erefPredicted.getEOperations());
		classLevelMetrics.put("class_operations_model1", erefOriginal.getEOperations().size());
		classLevelMetrics.put("class_operations_model2", erefPredicted.getEOperations().size());
		classLevelMetrics.put("class_operations_diff_model1_minus_model2", (Integer) classLevelMetrics.get("class_operations_model1") - (Integer) classLevelMetrics.get("class_operations_model2"));
		classLevelMetrics.put("class_operations_diff_model2_minus_model1", (Integer) classLevelMetrics.get("class_operations_model2") - (Integer) classLevelMetrics.get("class_operations_model1"));
		classLevelMetrics.put("operations_tp", operationResultObject.matched.size());
		classLevelMetrics.put("operations_fp", operationResultObject.onlyInModel2.size());
		classLevelMetrics.put("operations_fn", operationResultObject.onlyInModel1.size());

		VenDiagramDTO<EClass> superTypesResultObject = comparisonService.getVenDiagramForClasses(erefOriginal.getESuperTypes(), erefPredicted.getESuperTypes());
		classLevelMetrics.put("class_superTypes_model1", erefOriginal.getESuperTypes().size());
		classLevelMetrics.put("class_superTypes_model2", erefPredicted.getESuperTypes().size());
		classLevelMetrics.put("class_superTypes_diff_model1_minus_model2", (Integer) classLevelMetrics.get("class_superTypes_model1") - (Integer) classLevelMetrics.get("class_superTypes_model2"));
		classLevelMetrics.put("class_superTypes_diff_model2_minus_model1", (Integer) classLevelMetrics.get("class_superTypes_model2") - (Integer) classLevelMetrics.get("class_superTypes_model1"));
		classLevelMetrics.put("superTypes_tp", superTypesResultObject.matched.size());
		classLevelMetrics.put("superTypes_fp", superTypesResultObject.onlyInModel2.size());
		classLevelMetrics.put("superTypes_fn", superTypesResultObject.onlyInModel1.size());

		Integer truePositives = 0;
		Integer falsePositives = 0;
		Integer falseNegatives = 0;
		for(String entity: includedElements) {
			truePositives = truePositives + (Integer) classLevelMetrics.get(entity + "_tp");
			falsePositives = falsePositives + (Integer) classLevelMetrics.get(entity + "_fp");
			falseNegatives = falseNegatives + (Integer) classLevelMetrics.get(entity + "_fn");
		}
		classLevelMetrics.put("aggregate_tp", truePositives);
		classLevelMetrics.put("aggregate_fp", falsePositives);
		classLevelMetrics.put("aggregate_fn", falseNegatives);
		classLevelMetrics.put("aggregate_class_precision", MetricsComputationService.computePrecision((float) truePositives, (float) falsePositives));
		classLevelMetrics.put("aggregate_class_recall", MetricsComputationService.computeRecall(truePositives, falseNegatives));
		classLevelMetrics.put("aggregate_class_f1_score", MetricsComputationService.computeF1Score((float) classLevelMetrics.get("aggregate_class_precision"), (float) classLevelMetrics.get("aggregate_class_recall")));
		classLevelMetrics.put("semantic_similarity_average", -1);

		return classLevelMetrics;
	}

	public HashMap<String, Object> getModelLevelMetricsFromClassLevelMetrics(
			HashMap<String, HashMap<String, Object>> allMatchedClassesMetrics,
			ArrayList<HashMap<String, Integer>> allOriginalClassesMetricsNotMatched,
			ArrayList<HashMap<String, Integer>> allPredictedClassesMetricsNotMatched,
			HashMap<String, Integer> enumerationConfusionMatrix,
			Integer total_enumerations_model1,
			Integer total_enumerations_model2
		) {
			YamtlService countEngine = new YamtlService();

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
			if (this.modelComparisonConfiguration.INCLUDE_ENUMS) {
				modelLevelMetrics.put("enumerations_tp", enumerationConfusionMatrix.get("tp"));
				modelLevelMetrics.put("enumerations_fp", enumerationConfusionMatrix.get("fp"));
				modelLevelMetrics.put("enumerations_fn", enumerationConfusionMatrix.get("fn"));
				modelLevelMetrics.put("total_enumerations_model1", total_enumerations_model1);
				modelLevelMetrics.put("total_enumerations_model2", total_enumerations_model2);
				modelLevelMetrics.put("total_enumerations_diff_model1_minus_model2", total_enumerations_model1 - total_enumerations_model2);
				modelLevelMetrics.put("total_enumerations_diff_model2_minus_model1", total_enumerations_model2 - total_enumerations_model1);
			}

			for (String metric : includedElements) {
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

			for (String metric : includedElements) {
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
			int truePositives_aggregate = 0, falsePositives_aggregate = 0, falseNegatives_aggregate = 0;
			for (String metric : elementsIncludedInScoring) {
				truePositives_aggregate += (Integer) modelLevelMetrics.get(metric + "_tp");
				falsePositives_aggregate += (Integer) modelLevelMetrics.get(metric + "_fp");
				falseNegatives_aggregate += (Integer) modelLevelMetrics.get(metric + "_fn");
			}
			modelLevelMetrics.put("aggregate_tp", truePositives_aggregate);
			modelLevelMetrics.put("aggregate_fp", falsePositives_aggregate);
			modelLevelMetrics.put("aggregate_fn", falseNegatives_aggregate);
			modelLevelMetrics.put("aggregate_model_precision", MetricsComputationService.computePrecision((float) truePositives_aggregate, (float) falsePositives_aggregate));
			modelLevelMetrics.put("aggregate_model_recall", MetricsComputationService.computeRecall(truePositives_aggregate, falseNegatives_aggregate));
			modelLevelMetrics.put("aggregate_model_f1_score", MetricsComputationService.computeF1Score((float) modelLevelMetrics.get("aggregate_model_precision"), (float) modelLevelMetrics.get("aggregate_model_recall")));
			modelLevelMetrics.put("semantic_similarity_average", -1);

			return modelLevelMetrics;
		}

	public HashMap<String, JSONObject> compareModels(ArrayList<HashMap<String, String>> models, Boolean includeDependencies) {
		long startTime = System.currentTimeMillis();

		// initialize class level analysis object for matched classes
		HashMap<String, Object> allMatchedClassesMetrics = new HashMap<String, Object>();

		// initialize model level analysis object
		HashMap<String, Object> modelLevelResultObject = new HashMap<String, Object>();

		// perform class level comparison
		for(HashMap<String, String> model: models) {
			YamtlService countEngine = new YamtlService();
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

			ArrayList<EClass> classesModel1 = new ArrayList<EClass>();
			ArrayList<EClass> classesModel2 = new ArrayList<EClass>();
			for (Object classObject: allClassLiteralsForOriginalModel) {
				EClass eclass = (EClass) classObject;
				classesModel1.add(eclass);
			}
			for (Object classObject: allClassLiteralsForGeneratedlModel) {
				EClass eclass = (EClass) classObject;
				classesModel2.add(eclass);
			}
			VenDiagramDTO<EClass> venDiagramClasses = comparisonService.getVenDiagramForClasses(classesModel1, classesModel2);

			// initialize class level analysis object
			HashMap<String, HashMap<String, Object>> matchedClassMetrics = new HashMap<String, HashMap<String, Object>>();

			// initialize class level analysis object for original classes not matched
			ArrayList<HashMap<String, Integer>> allOriginalClassesMetricsNotMatched = new ArrayList<HashMap<String, Integer>>();

			// initialize class level analysis object for predicted classes not matched
			ArrayList<HashMap<String, Integer>> allPredictedClassesMetricsNotMatched = new ArrayList<HashMap<String, Integer>>();

			for (MatchedElementsDTO<EClass> eclassesMatchedPairs: venDiagramClasses.matched) {
				EClass erefOriginal = eclassesMatchedPairs.model1;
				EClass erefPredicted = eclassesMatchedPairs.model2;
				HashMap<String, Object> classLevelMetrics = getClassLevelMetrics(erefOriginal, erefPredicted);
				matchedClassMetrics.put(erefOriginal.getName(), classLevelMetrics);
			}

			for (EClass erefOriginal: venDiagramClasses.onlyInModel1) {
				HashMap<String, Integer> metricsNotMatched = new HashMap<String, Integer>();
				if (modelComparisonConfiguration.INCLUDE_CLASS_ATTRIBUTES) {
					metricsNotMatched.put(Constants.ATTRIBUTES_IDENTIFIER, erefOriginal.getEAttributes().size());
				}
				if (modelComparisonConfiguration.INCLUDE_CLASS_REFERENCES) {
					metricsNotMatched.put(Constants.REFERENCES_IDENTIFIER, erefOriginal.getEReferences().size());
				}
				if (modelComparisonConfiguration.INCLUDE_CLASS_OPERATIONS) {
					metricsNotMatched.put(Constants.OPERATIONS_IDENTIFIER, erefOriginal.getEOperations().size());
				}
				if (modelComparisonConfiguration.INCLUDE_CLASS_SUPERTYPES) {
					metricsNotMatched.put(Constants.SUPERTYPES_IDENTIFIER, erefOriginal.getESuperTypes().size());
				}
				allOriginalClassesMetricsNotMatched.add(metricsNotMatched);
			}

			for (EClass erefPredicted: venDiagramClasses.onlyInModel2) {
				HashMap<String, Integer> metricsNotMatched = new HashMap<String, Integer>();
				if (modelComparisonConfiguration.INCLUDE_CLASS_ATTRIBUTES) {
					metricsNotMatched.put(Constants.ATTRIBUTES_IDENTIFIER, erefPredicted.getEAttributes().size());
				}
				if (modelComparisonConfiguration.INCLUDE_CLASS_REFERENCES) {
					metricsNotMatched.put(Constants.REFERENCES_IDENTIFIER, erefPredicted.getEReferences().size());
				}
				if (modelComparisonConfiguration.INCLUDE_CLASS_OPERATIONS) {
					metricsNotMatched.put(Constants.OPERATIONS_IDENTIFIER, erefPredicted.getEOperations().size());
				}
				if (modelComparisonConfiguration.INCLUDE_CLASS_SUPERTYPES) {
					metricsNotMatched.put(Constants.SUPERTYPES_IDENTIFIER, erefPredicted.getESuperTypes().size());
				}
				allPredictedClassesMetricsNotMatched.add(metricsNotMatched);				
			}

			// compute confusion matrix for enumerations
			ArrayList<EEnum> enumsModel1 = new ArrayList<EEnum>();
			ArrayList<EEnum> enumsModel2 = new ArrayList<EEnum>();
			for (Object enumObject: countEngine.getAllLiterals(original_model).get("enumerations")) {
				EEnum eenum = (EEnum) enumObject;
				enumsModel1.add(eenum);
			}
			for (Object enumObject: countEngine.getAllLiterals(generated_model).get("enumerations")) {
				EEnum eenum = (EEnum) enumObject;
				enumsModel2.add(eenum);
			}
			VenDiagramDTO<EEnum> enumerationResultObject = comparisonService.getVenDiagramForEnumerations(enumsModel1, enumsModel2);
			HashMap<String, Integer> enumerationConfusionMatrix = new HashMap<String, Integer>();
			enumerationConfusionMatrix.put("tp", enumerationResultObject.matched.size());
			enumerationConfusionMatrix.put("fn", enumerationResultObject.onlyInModel1.size());
			enumerationConfusionMatrix.put("fp", enumerationResultObject.onlyInModel2.size());
			Integer total_enumerations_model1 = original_literal_counts.get("enumerations");
			Integer total_enumerations_model2 = generated_literal_counts.get("enumerations");

			// Create model level metrics object informed from class level analysis
			System.out.println("Generating Model Level Metrics");
			HashMap<String, Object> modelLevelMetrics = getModelLevelMetricsFromClassLevelMetrics(
				matchedClassMetrics, 
				allOriginalClassesMetricsNotMatched,
				allPredictedClassesMetricsNotMatched,
				enumerationConfusionMatrix,
				total_enumerations_model1,
				total_enumerations_model2
			);
			modelLevelMetrics.put("model1_identifier", original_model);
			modelLevelMetrics.put("model2_identifier", generated_model);
			modelLevelResultObject.put(model.get("projectName"), modelLevelMetrics);
			allMatchedClassesMetrics.put(model.get("projectName"), matchedClassMetrics);
		}

		System.out.println("Generating Final JSON");
		JSONObject jsonResultsClass = new JSONObject(allMatchedClassesMetrics);
		JSONObject jsonResultsModel = new JSONObject(modelLevelResultObject);
		HashMap<String, JSONObject> results = new HashMap<String, JSONObject>();
		results.put("classLevelJson", jsonResultsClass);
		results.put("modelLevelJson", jsonResultsModel);
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		JSONObject performance = new JSONObject();
		performance.put("time in milliseconds for syntantic comparison", Long.toString(duration));
		results.put("time", performance);
		return results;
	}

}
