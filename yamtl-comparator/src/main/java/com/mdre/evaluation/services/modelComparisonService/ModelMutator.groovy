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
        return this.falsePositiveClasses
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
                        ct.name = c.name + "abcdefs"
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
                }),
            rule('Operation')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EOperation)
                .out('ct', EcorePackage.eINSTANCE.EOperation, {
                    operation_count = operation_count + 1
                    ct.name = c.name
                }),
            rule('Reference')
                .isLazy()
                .in('c', EcorePackage.eINSTANCE.EReference)
                .out('ct', EcorePackage.eINSTANCE.EReference, {
                    reference_count = reference_count + 1
                    ct.name = c.name
                    ct.lowerBound = c.lowerBound
                    ct.upperBound = c.upperBound
                    ct.containment = c.containment
                    ct.ordered = c.ordered
                    ct.unique = c.unique
                })
        ])
	}

    public static createMutantsForTesting(String modelPath) {
        System.out.println("Creating Instance");
		def matcher = new ModelMutator()
		
        System.out.println("Loading Input models");
		matcher.loadInputModels([
            'in': modelPath, 
        ])

        System.out.println("Executing Matcher");
		matcher.execute()

        String modelDirectory = modelPath.substring(0, modelPath.lastIndexOf("/")) + "/mutant1/"   
        String outputModel = modelDirectory + "mutant_1.ecore"
		matcher.saveOutputModels(['out': outputModel])

        def jsonBuilder = new JsonBuilder()
        println("creating json")
        def root = jsonBuilder.results {
            classes_fp 10
            attributes_fp 12
            references_fp 9
            operations_fp 21
        }
        String jsonString = JsonOutput.prettyPrint(jsonBuilder.toString())
        File jsonFile = new File(modelDirectory + "expected_results.json")
        jsonFile.write(jsonString)
        println("JSON file created successfully!")
    }

    public static void run() {
        createMutantsForTesting("/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/yamtl-comparator/src/main/resources/bt_openlink/bt_openlink.ecore")
    }
}