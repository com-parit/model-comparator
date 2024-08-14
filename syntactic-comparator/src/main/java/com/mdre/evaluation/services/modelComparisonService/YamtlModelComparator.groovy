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

public class YamtlModelComparator extends YAMTLModuleGroovy {
	
	public YamtlModelComparator() {
        // YAMTLGroovyExtensions_dynamicEMF.init(this)
		header().in('model1', EcorePackage.eINSTANCE).in('model2', EcorePackage.eINSTANCE).out('out', EcorePackage.eINSTANCE)
		
		ruleStore([
			rule('MatchClass')
				.in('p1', "model1", EcorePackage.eINSTANCE.EClass)
				.in('p2', "model2", EcorePackage.eINSTANCE.EClass).filter { p1.name == p2.name }
				.out('new_p', "out", EcorePackage.eINSTANCE.EClass, {
					new_p.name = p1.name
				})
		])
	}

    public static void run() {
		def modelPath1 = "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/src/main/resources/bt_openlink/mutant_1/mutant_1.ecore"
		def modelPath2 = "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/src/main/resources/bt_openlink/mutant_1/mutant_1.ecore"
        def outputModel = "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/syntactic-comparator/src/main/resources/bt_openlink/output.ecore"
		System.out.println("Creating Instance");
		def matcher = new YamtlModelComparator()
        System.out.println("Loading Input models");
		matcher.loadInputModels([
            'model1': modelPath1, 
            'model2': modelPath2, 
        ])
        System.out.println("Executing Matcher");
		matcher.execute()
		matcher.saveOutputModels(['out': outputModel])
	}
}