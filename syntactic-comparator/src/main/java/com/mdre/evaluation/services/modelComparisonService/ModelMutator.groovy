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
import com.mdre.evaluation.dtos.MutantCreationDTO;

public class ModelMutator extends YAMTLModuleGroovy {
	
    public class_fn_count = 0
    public attribute_fn_count = 0
    public reference_fn_count = 0
    public operation_fn_count = 0
    public class_fp_count = 0
    public attribute_fp_count = 0
    public reference_fp_count = 0
    public operation_fp_count = 0

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

    public falseNegativeClasses = 0
    public getfalseNegativeClasses() {
        return this.falseNegativeClasses
    }
    public setfalseNegativeClasses(int value) {
        return this.falseNegativeClasses = value
    }

    public falseNegativeOperations = 0
    public getfalseNegativeOperations() {
        return this.falseNegativeOperations
    }
    public setfalseNegativeOperations(int value) {
        return this.falseNegativeOperations = value
    }

    public falseNegativeAttributes = 0
    public getfalseNegativeAttributes() {
        return this.falseNegativeAttributes
    }
    public setfalseNegativeAttributes(int value) {
        return this.falseNegativeAttributes = value
    }

    public falseNegativeReferences = 0
    public getfalseNegativeReferences() {
        return this.falseNegativeReferences
    }
    public setfalseNegativeReferences(int value) {
        return this.falseNegativeReferences = value
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
                    for (int i = 0; i < new_classes.size(); i++) {
                        if (new_classes[i].name != "mutation_false_negative") {
                            t.EClassifiers.add(new_classes[i])
                        } else {
                            if (class_fp_count < falsePositiveClasses) {
                                class_fp_count = class_fp_count + 1
                                t.EClassifiers.add(new_classes[i])
                            }
                        }
                    }
                }),
            rule('Class')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EClass)
                .out('ct', EcorePackage.eINSTANCE.EClass, {
                    if (class_fn_count < falseNegativeClasses) {
                        class_fn_count = class_fn_count + 1
                            ct.name = "mutation_false_negative"
                    }
                    else {
                        ct.name = c.name
                        def new_attributes = fetch(c.eStructuralFeatures, 'ct', 'Attribute')
                        def operations = fetch(c.eOperations, 'ct', 'Operation')
                        def references = fetch(c.eStructuralFeatures, 'ct', 'Reference')
                        for (int i = 0; i < new_attributes.size(); i++) {
                            if (new_attributes[i].name != "mutation_false_negative"){
                                ct.EStructuralFeatures.add(new_attributes[i])
                            } else {
                                if (attribute_fp_count < falsePositiveAttributes) {
                                    attribute_fp_count = attribute_fp_count + 1
                                    ct.EStructuralFeatures.add(new_attributes[i])
                                }                                    
                            }
                        }
                        for (int i = 0; i < operations.size(); i++) {
                            if (operations[i].name != "mutation_false_negative"){
                                ct.EOperations.add(operations[i])
                            } else{
                                if (operation_fp_count < falsePositiveOperations) {
                                    operation_fp_count = operation_fp_count + 1
                                    ct.EOperations.add(operations[i])
                                }                                    
                            }
                        }
                        for (int i = 0; i < references.size(); i++) {
                            if (references[i].name != "mutation_false_negative") {
                                ct.EStructuralFeatures.add(references[i])
                            } else{
                                if (reference_fp_count < falsePositiveReferences) {
                                    reference_fp_count = reference_fp_count + 1
                                    ct.EStructuralFeatures.add(references[i])
                                }                                    
                            }
                        }
                    }
                }),
            rule('Attribute')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EAttribute)
                .out('ct', EcorePackage.eINSTANCE.EAttribute, {
                    if (attribute_fn_count < falseNegativeAttributes) {
                        attribute_fn_count = attribute_fn_count + 1
                        ct.name = "mutation_false_negative"
                    }
                    else {
                        ct.name = c.name
                        ct.EType = c.EType
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
                    if (operation_fn_count < falseNegativeOperations) {
                        operation_fn_count = operation_fn_count + 1
                        ct.name = "mutation_false_negative"
                    }
                    else {
                        ct.name = c.name
                    }
                }),
            rule('Reference')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EReference)
                .out('ct', EcorePackage.eINSTANCE.EReference, {
                    if (reference_fn_count < falseNegativeReferences) {
                        reference_fn_count = reference_fn_count + 1
                        ct.name = "mutation_false_negative"
                    }
                    else {
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

    public static createMutant(MutantCreationDTO mutantCreationObject) {
        def String modelPath = mutantCreationObject.modelPath 
        def int falseNegativeClassesPercentange = mutantCreationObject.falseNegativeClassesPercentange 
        def int falseNegativeAttributesPercentange = mutantCreationObject.falseNegativeAttributesPercentange 
        def int falseNegativeOperationsPercentange = mutantCreationObject.falseNegativeOperationsPercentange 
        def int falseNegativeReferencesPercentange = mutantCreationObject.falseNegativeReferencesPercentange
        def String key = mutantCreationObject.key


        def literalCounts = ModelElementsFetcher.getCountOfAllLiterals(modelPath)
        // mutant 1 (exact match)
        def total_classes = literalCounts.get("classes")
        def total_attributes = literalCounts.get("attributes")
        def total_references = literalCounts.get("references")
        def total_operations = literalCounts.get("operations")
        int falseNegativeClasses = Math.ceil(total_classes * (falseNegativeClassesPercentange / 100)) as int
        int falseNegativeAttributes = Math.ceil(total_attributes * (falseNegativeAttributesPercentange / 100)) as int
        int falseNegativeOperations = Math.ceil(total_operations * (falseNegativeOperationsPercentange / 100)) as int
        int falseNegativeReferences = Math.ceil(total_references * (falseNegativeReferencesPercentange / 100)) as int
        int falsePositiveClasses = falseNegativeClasses
        int falsePositiveAttributes = 0
        if (falseNegativeAttributes != 0) {
            falsePositiveAttributes = Math.floor((falseNegativeAttributes) * 0.40) as int
        }
        int falsePositiveOperations = 0
        if (falseNegativeOperations != 0) {
            falsePositiveOperations = Math.floor((falseNegativeOperations) * 0.40) as int
        }
        int falsePositiveReferences = 0
        if (falseNegativeReferences != 0) {
            falsePositiveReferences = Math.floor((falseNegativeReferences) * 0.40) as int
        }

        System.out.println("Creating Instance");
		def matcher = new ModelMutator()
        matcher.setfalsePositiveClasses(falsePositiveClasses)
        matcher.setfalsePositiveAttributes(falsePositiveAttributes)
        matcher.setfalsePositiveOperations(falsePositiveOperations)
        matcher.setfalsePositiveReferences(falsePositiveReferences)
        matcher.setfalseNegativeClasses(falseNegativeClasses)
        matcher.setfalseNegativeAttributes(falseNegativeAttributes)
        matcher.setfalseNegativeOperations(falseNegativeOperations)
        matcher.setfalseNegativeReferences(falseNegativeReferences)
		
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

        def classes_tp_v = total_classes - falseNegativeClasses
        def attributes_tp_v = total_attributes - falseNegativeAttributes
        def operations_tp_v = total_operations - falseNegativeOperations
        def references_tp_v = total_references - falseNegativeReferences
        def aggregate_tp_v = classes_tp_v + attributes_tp_v + operations_tp_v + references_tp_v
        def aggregate_fp_v = falsePositiveClasses + falsePositiveAttributes + falsePositiveOperations + falsePositiveReferences
        def aggregate_fn_v = falseNegativeClasses + falseNegativeAttributes + falseNegativeOperations + falseNegativeReferences
        def aggregate_model_precision_v = 0
        if (aggregate_tp_v != 0 || aggregate_fp_v != 0) {
            aggregate_model_precision_v = (aggregate_tp_v) / (aggregate_tp_v + aggregate_fp_v) 
        }
        def aggregate_model_recall_v = 0
        if (aggregate_tp_v != 0 || aggregate_fn_v != 0) {
            aggregate_model_recall_v = (aggregate_tp_v) / (aggregate_tp_v + aggregate_fn_v)
        }
        def aggregate_model_f1_score_v = 0
        if (aggregate_model_precision_v + aggregate_model_recall_v != 0) {
            aggregate_model_f1_score_v = 2 * (aggregate_model_precision_v * aggregate_model_recall_v) / (aggregate_model_precision_v + aggregate_model_recall_v)
        }
        println("creating json")
        def root = jsonBuilder.results {
            classes_fp falsePositiveClasses
            classes_tp classes_tp_v
            classes_fn falseNegativeClasses
            attributes_fp falsePositiveAttributes
            attributes_tp attributes_tp_v
            attributes_fn falseNegativeAttributes
            operations_fp falsePositiveOperations
            operations_tp operations_tp_v
            operations_fn falseNegativeOperations
            references_fp falsePositiveReferences
            references_tp references_tp_v
            references_fn falseNegativeReferences
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
        MutantCreationDTO mutantCreationObject1 = new MutantCreationDTO();
        mutantCreationObject1.modelPath = modelPath
        mutantCreationObject1.falseNegativeClassesPercentange = 0
        mutantCreationObject1.falseNegativeAttributesPercentange = 0
        mutantCreationObject1.falseNegativeOperationsPercentange = 0
        mutantCreationObject1.falseNegativeReferencesPercentange = 0
        mutantCreationObject1.key = "mutant_1"
        createMutant(mutantCreationObject1)

        // opposite
        MutantCreationDTO mutantCreationObject2 = new MutantCreationDTO();
        mutantCreationObject2.modelPath = modelPath
        mutantCreationObject2.falseNegativeClassesPercentange = 100
        mutantCreationObject2.falseNegativeAttributesPercentange = 100
        mutantCreationObject2.falseNegativeOperationsPercentange = 100
        mutantCreationObject2.falseNegativeReferencesPercentange = 100
        mutantCreationObject2.key = "mutant_2"
        createMutant(mutantCreationObject2)

        // classes match only
        MutantCreationDTO mutantCreationObject3 = new MutantCreationDTO();
        mutantCreationObject3.modelPath = modelPath
        mutantCreationObject3.falseNegativeClassesPercentange = 0
        mutantCreationObject3.falseNegativeAttributesPercentange = 100
        mutantCreationObject3.falseNegativeOperationsPercentange = 100
        mutantCreationObject3.falseNegativeReferencesPercentange = 100
        mutantCreationObject3.key = "mutant_3"
        createMutant(mutantCreationObject3)

        // classes and attributes match only
        MutantCreationDTO mutantCreationObject4 = new MutantCreationDTO();
        mutantCreationObject4.modelPath = modelPath
        mutantCreationObject4.falseNegativeClassesPercentange = 0
        mutantCreationObject4.falseNegativeAttributesPercentange = 0
        mutantCreationObject4.falseNegativeOperationsPercentange = 100
        mutantCreationObject4.falseNegativeReferencesPercentange = 100
        mutantCreationObject4.key = "mutant_4"
        createMutant(mutantCreationObject4)

        // classes and references match only
        MutantCreationDTO mutantCreationObject5 = new MutantCreationDTO();
        mutantCreationObject5.modelPath = modelPath
        mutantCreationObject5.falseNegativeClassesPercentange = 0
        mutantCreationObject5.falseNegativeAttributesPercentange = 100
        mutantCreationObject5.falseNegativeOperationsPercentange = 100
        mutantCreationObject5.falseNegativeReferencesPercentange = 0
        mutantCreationObject5.key = "mutant_5"
        createMutant(mutantCreationObject5)

        // classes and operations match only
        MutantCreationDTO mutantCreationObject6 = new MutantCreationDTO();
        mutantCreationObject6.modelPath = modelPath
        mutantCreationObject6.falseNegativeClassesPercentange = 0
        mutantCreationObject6.falseNegativeAttributesPercentange = 100
        mutantCreationObject6.falseNegativeOperationsPercentange = 0
        mutantCreationObject6.falseNegativeReferencesPercentange = 100
        mutantCreationObject6.key = "mutant_6"
        createMutant(mutantCreationObject6)

        // classes, attributes and operations match only
        MutantCreationDTO mutantCreationObject7 = new MutantCreationDTO();
        mutantCreationObject7.modelPath = modelPath
        mutantCreationObject7.falseNegativeClassesPercentange = 0
        mutantCreationObject7.falseNegativeAttributesPercentange = 0
        mutantCreationObject7.falseNegativeOperationsPercentange = 0
        mutantCreationObject7.falseNegativeReferencesPercentange = 100
        mutantCreationObject7.key = "mutant_7"
        createMutant(mutantCreationObject7)

        // classes, attributes and references match only
        MutantCreationDTO mutantCreationObject8 = new MutantCreationDTO();
        mutantCreationObject8.modelPath = modelPath
        mutantCreationObject8.falseNegativeClassesPercentange = 0
        mutantCreationObject8.falseNegativeAttributesPercentange = 0
        mutantCreationObject8.falseNegativeOperationsPercentange = 100
        mutantCreationObject8.falseNegativeReferencesPercentange = 0
        mutantCreationObject8.key = "mutant_8"
        createMutant(mutantCreationObject8)

        // classes, operations and references match only
        MutantCreationDTO mutantCreationObject9 = new MutantCreationDTO();
        mutantCreationObject9.modelPath = modelPath
        mutantCreationObject9.falseNegativeClassesPercentange = 0
        mutantCreationObject9.falseNegativeAttributesPercentange = 100
        mutantCreationObject9.falseNegativeOperationsPercentange = 0
        mutantCreationObject9.falseNegativeReferencesPercentange = 0
        mutantCreationObject9.key = "mutant_9"
        createMutant(mutantCreationObject9)

        // hybrid match
        MutantCreationDTO mutantCreationObject10 = new MutantCreationDTO();
        mutantCreationObject10.modelPath = modelPath
        mutantCreationObject10.falseNegativeClassesPercentange = 0
        mutantCreationObject10.falseNegativeAttributesPercentange = 30
        mutantCreationObject10.falseNegativeOperationsPercentange = 10
        mutantCreationObject10.falseNegativeReferencesPercentange = 20
        mutantCreationObject10.key = "mutant_10"
        createMutant(mutantCreationObject10)
    }

    public static void run() {
        File mainDirectory = new File("//mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/user-interface/evaluate_travis");

        if (mainDirectory.exists() && mainDirectory.isDirectory()) {
            File[] subdirs = mainDirectory.listFiles();
            for (File subdir : subdirs) {
                if (subdir.isDirectory()) {
                    File[] files = subdir.listFiles();
                    for (File file : files) {
                        try {
                            String path = file.getAbsolutePath()
                            if (!file.isDirectory() && path.substring(path.lastIndexOf(".")) == ".ecore") {
                                MutantCreationDTO mutantCreationObject = new MutantCreationDTO();
                                mutantCreationObject.modelPath = path.toString()
                                mutantCreationObject.falseNegativeClassesPercentange = 0
                                mutantCreationObject.falseNegativeAttributesPercentange = 0
                                mutantCreationObject.falseNegativeOperationsPercentange = 0
                                mutantCreationObject.falseNegativeReferencesPercentange = 0
                                mutantCreationObject.key = "base_model"
                                createMutant(mutantCreationObject)
                                String baseModelPath = path.substring(0, path.lastIndexOf("/")) + "/base_model/base_model.ecore";
                                createMutantsForTesting(baseModelPath)
                            }
                        } catch (Exception e) {
                            System.out.println("Could not generate mutants for " + file.getAbsolutePath() + "/n" + e)
                        }
                    }
                }
            }
        } else {
            System.out.println("The provided path is not a valid directory.");
        }
    }
}