package com.mdre.evaluation.services.modelComparisonService;

import static yamtl.dsl.Rule.*

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EcorePackage
import yamtl.groovy.YAMTLGroovyExtensions_dynamicEMF
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.emf.common.util.BasicEList

import yamtl.core.YAMTLModuleGroovy
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import java.io.File
import com.mdre.evaluation.services.modelComparisonService.ModelElementsFetcher

public class ModelMutator extends YAMTLModuleGroovy {
	
    public class_count = 0
    public attribute_count = 0
    public reference_count = 0
    public operation_count = 0

    public falsePositiveClasses = 0
    public getfalsePositiveClasses() {
        return this.falsePositiveClasses
    }
    public setfalsePositiveClasses(int value) {
        return this.falsePositiveClasses = value
    }

    public falsePositiveOperations = 0
    public getfalsePositiveOperations() {
        return this.falsePositiveOperations
    }
    public setfalsePositiveOperations(int value) {
        return this.falsePositiveOperations = value
    }

    public falsePositiveAttributes = 0
    public getfalsePositiveAttributes() {
        return this.falsePositiveAttributes
    }
    public setfalsePositiveAttributes(int value) {
        return this.falsePositiveAttributes = value
    }

    public falsePositiveReferences = 0
    public getfalsePositiveReferences() {
        return this.falsePositiveReferences
    }
    public setfalsePositiveReferences(int value) {
        return this.falsePositiveReferences = value
    }

	public ModelMutator() {
        System.out.println("Initializing");
		
        YAMTLGroovyExtensions_dynamicEMF.init(this)

        header().in('in', EcorePackage.eINSTANCE).out('out', EcorePackage.eINSTANCE)
        ruleStore([
            rule('Package')
                .in('s', EcorePackage.eINSTANCE.EPackage)
                .out('t', EcorePackage.eINSTANCE.EPackage, {
					t.name = s.name
                    def new_classes = fetch(s.eClassifiers, 'ct', 'Class')
                    t.eClassifiers = s.eClassifiers
                    t.eClassifiers.clear()
                    for (int i = 0; i < new_classes.size(); i++) {
                        t.eClassifiers.add(new_classes[i])
                    }
                }),
            rule('Class')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EClass)
                .out('ct', EcorePackage.eINSTANCE.EClass, {
                    class_count = class_count + 1
                    if (class_count < falsePositiveClasses) {
                        ct.name = "mutation"
                    }
                    else {
                        ct.name = c.name
                        def new_classes = fetch(c.eStructuralFeatures, 'ct', 'Attribute')
                        def operations = fetch(c.eOperations, 'ct', 'Operation')
                        def references = fetch(c.eStructuralFeatures, 'ct', 'Reference')
                        ct.eStructuralFeatures = c.eStructuralFeatures
                        ct.eStructuralFeatures.clear()
                        ct.eOperations = c.eOperations
                        ct.eOperations.clear()
                        for (int i = 0; i < new_classes.size(); i++) {
                            ct.eStructuralFeatures.add(new_classes[i])
                        }
                        for (int i = 0; i < operations.size(); i++) {
                            ct.eOperations.add(operations[i])
                        }
                        for (int i = 0; i < references.size(); i++) {
                            ct.eStructuralFeatures.add(references[i])
                        }
                    }
                }),
            rule('Attribute')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EAttribute)
                .out('ct', EcorePackage.eINSTANCE.EAttribute, {
                    attribute_count = attribute_count + 1
                    if (attribute_count < falsePositiveAttributes) {
                        ct.name = "mutation"
                    } else {
                        ct.name = c.name
                        // def new_type = c.getEType()
                        // try {
                        //     ct.setEType(new_type)
                        // } catch (Exception e) {
                        //     System.out.println("?")
                        //     System.out.println(e)
                        // }
                        ct.lowerBound = c.lowerBound
                        ct.upperBound = c.upperBound
                        ct.ordered = c.ordered
                        ct.unique = c.unique
                    }
                }),
            rule('Operation')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EOperation)
                .out('ct', EcorePackage.eINSTANCE.EOperation, {
                    operation_count = operation_count + 1
                    if (operation_count < falsePositiveOperations) {
                        ct.name = "mutation"
                    } else {
                        ct.name = c.name
                    }
                }),
            rule('Reference')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EReference)
                .out('ct', EcorePackage.eINSTANCE.EReference, {
                    reference_count = reference_count + 1
                    if (reference_count < falsePositiveReferences) {
                        ct.name = "mutation"
                    } else {
                        ct.name = c.name
                        ct.lowerBound = c.lowerBound
                        ct.upperBound = c.upperBound
                        ct.containment = c.containment
                        ct.ordered = c.ordered
                        ct.unique = c.unique
                    }
                })
        ])
	}

    public static createMutant(
        String modelPath, 
        falsePositiveClassesPercentange, 
        falsePositiveAttributesPercentange, 
        falsePositiveOperationsPercentange, 
        falsePositiveReferencesPercentange,
        key
    ) {
        def literalCounts = ModelElementsFetcher.getCountOfAllLiterals(modelPath)

        // mutant 1 (exact match)
        def total_classes = literalCounts.get("classes")
        def total_attributes = literalCounts.get("attributes")
        def total_references = literalCounts.get("references")
        def total_operations = literalCounts.get("operations")
        int falsePositiveClasses = (int) (total_classes * (falsePositiveClassesPercentange / 100))
        int falsePositiveAttributes = (int) (total_attributes * (falsePositiveAttributesPercentange / 100))
        int falsePositiveOperations = (int) (total_operations * (falsePositiveOperationsPercentange / 100))
        int falsePositiveReferences = (int) (total_references * (falsePositiveReferencesPercentange / 100))

        System.out.println("Creating Instance");
		def matcher = new ModelMutator()
        matcher.setfalsePositiveClasses(falsePositiveClasses)
        matcher.setfalsePositiveAttributes(falsePositiveAttributes)
        matcher.setfalsePositiveOperations(falsePositiveOperations)
        matcher.setfalsePositiveReferences(falsePositiveReferences)
		
        System.out.println("Loading Input models");
		matcher.loadInputModels([
            'in': modelPath, 
        ])
        System.out.println("Executing Matcher");
		matcher.execute()
        String modelDirectory = modelPath.substring(0, modelPath.lastIndexOf("/")) + "/" + key + "/"   
        String outputModel = modelDirectory + key + ".ecore"
		matcher.saveOutputModels(['out': outputModel])

        def jsonBuilder = new JsonBuilder()
        def classes_tp_v = total_classes - falsePositiveClasses 
        def attributes_tp_v = total_attributes - falsePositiveAttributes 
        def operations_tp_v = total_operations - falsePositiveOperations 
        def references_tp_v = total_references - falsePositiveReferences 
        def aggregate_tp_v = classes_tp_v + attributes_tp_v + operations_tp_v + references_tp_v
        def aggregate_fp_v = falsePositiveClasses + falsePositiveAttributes + falsePositiveOperations + falsePositiveReferences
        def aggregate_fn_v = falsePositiveClasses + falsePositiveAttributes + falsePositiveOperations + falsePositiveReferences
        def aggregate_model_precision_v = (aggregate_tp_v) / (aggregate_tp_v + aggregate_fp_v) 
        def aggregate_model_recall_v = (aggregate_tp_v) / (aggregate_tp_v + aggregate_fn_v)
        def aggregate_model_f1_score_v = 0
        if (aggregate_model_precision_v + aggregate_model_recall_v != 0) {
            aggregate_model_f1_score_v = 2 * (aggregate_model_precision_v * aggregate_model_recall_v) / (aggregate_model_precision_v + aggregate_model_recall_v)
        }
        println("creating json")
        def root = jsonBuilder.results {
            classes_fp falsePositiveClasses
            classes_tp classes_tp_v
            classes_fn falsePositiveClasses
            attributes_fp falsePositiveAttributes
            attributes_tp attributes_tp_v
            attributes_fn falsePositiveAttributes
            operations_fp falsePositiveOperations
            operations_tp operations_tp_v
            operations_fn falsePositiveOperations
            references_fp falsePositiveReferences
            references_tp references_tp_v
            references_fn falsePositiveReferences
            aggregate_tp aggregate_tp_v
            aggregate_fp aggregate_fp_v
            aggregate_fn aggregate_fn_v
            aggregate_model_precision aggregate_model_precision_v 
            aggregate_model_recall aggregate_model_recall_v
            aggregate_model_f1_score aggregate_model_f1_score_v
        }
        String jsonString = JsonOutput.prettyPrint(jsonBuilder.toString())
        File jsonFile = new File(modelDirectory + "expected_results.json")
        jsonFile.write(jsonString)
        println("JSON file created successfully!")
    }

    public static createMutantsForTesting(String modelPath) {
        // exact match
        createMutant(modelPath, 0, 0, 0, 0, "mutant_1")

        // opposite
        createMutant(modelPath, 100, 100, 100, 100, "mutant_2")

        // classes match only
        createMutant(modelPath, 0, 100, 100, 100, "mutant_3")

        // classes and attributes match only
        createMutant(modelPath, 0, 0, 100, 100, "mutant_4")

        // classes and references match only
        createMutant(modelPath, 0, 100, 100, 0, "mutant_5")

        // classes and operations match only
        createMutant(modelPath, 0, 100, 0, 100, "mutant_6")

        // classes, attributes and operations match only
        createMutant(modelPath, 0, 0, 0, 100, "mutant_7")

        // classes, attributes and references match only
        createMutant(modelPath, 0, 0, 100, 0, "mutant_8")

        // classes, operations and references match only
        createMutant(modelPath, 0, 100, 0, 0, "mutant_9")

        // hybrid match
        createMutant(modelPath, 0, 40, 30, 70, "mutant_10")
    }

    public static void run() {
        // exact match
        createMutant("/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/src/main/resources/sample/bt_openlink.ecore", 0, 0, 0, 0, "mutant_1")

        // File mainDirectory = new File("/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/evaluation/travis/evaluateTravis");

        // if (mainDirectory.exists() && mainDirectory.isDirectory()) {
        //     File[] subdirs = mainDirectory.listFiles();
        //     for (File subdir : subdirs) {
        //         if (subdir.isDirectory()) {
        //             File[] files = subdir.listFiles();
        //             for (File file : files) {
        //                 try {
        //                     String path = file.getAbsolutePath()
        //                     if (!file.isDirectory() && path.substring(path.lastIndexOf(".")) == ".ecore") {
        //                         createMutant(path.toString(), 0, 0, 0, 0, "base_model")
        //                         String baseModelPath = path.substring(0, path.lastIndexOf("/")) + "/base_model/base_model.ecore";
        //                         createMutantsForTesting(baseModelPath)
        //                     }
        //                 } catch (Exception e) {
        //                     System.out.println("Could not generate mutants for " + file.getAbsolutePath() + "/n" + e)
        //                 }
        //             }
        //         }
        //     }
        // } else {
        //     System.out.println("The provided path is not a valid directory.");
        // }
    }
}