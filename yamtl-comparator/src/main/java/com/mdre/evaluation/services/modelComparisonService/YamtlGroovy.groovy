package com.mdre.evaluation.services.modelComparisonService;

import static yamtl.dsl.Rule.*

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EcorePackage

import yamtl.core.YAMTLModuleGroovy

public class YamtlGroovy extends YAMTLModuleGroovy {
	
	public YamtlGroovy(EPackage CD) {
        System.out.println("Initializing");
		
		header().in('model1', CD).in('model2', CD).out('out', CD)

        System.out.println("Header Initialized");
		
		ruleStore([
			rule('MatchPackage')
				.in('p1', 'model1', CD.EPackage)
				// .in('p2', 'model2', CD.EPackage)
				.out('new_p', 'out', CD.EPackage, {
					new_p.name = p1.name
                    System.out.println(new_p.name);
					new_p.eClassifiers = fetch(p1.eClassifiers)
					// def new_c_list = fetch(['c1': p1.eClassifiers, 'c2': p2.eClassifiers])
					// new_p.eClassifiers.addAll(new_c_list)
				}),

			// rule('MatchDataType')
			// 	.in('c1', 'model1', CD.DataType)
			// 	.in('c2', 'model2', CD.DataType).filter { c1.name == c2.name }
			// 	.out('new_d', 'out', CD.DataType, {
			// 		new_d.name = c1.name
			// 	}),

			// rule('MatchClass')
			// 	.in('c1', 'model1', CD.EClass)
			// 	.in('c2', 'model2', CD.EClass).filter { }
			// 	.out('new_c', 'out', CD.EClass, {
            //         System.out.println(new_c.name)
			// 		new_c.name = c1.name
			// 		def new_a_list = fetch(['a1': c1.eAttributes, 'a2': c2.eAttributes])
			// 		new_c.eAttributes.addAll(new_a_list)
			// 	}),
			
			// rule('MatchAttribute')
			// 	.in('a1', 'model1', CD.EAttribute)
			// 	.in('a2', 'model2', CD.EAttribute)
			// 	.out('new_a', 'out', CD.EAttribute, {
			// 		new_a.name = a1.name
			// 	})
		])
	}

    public static void run() {
        System.out.println("Preloading metamodel");
		def resSM = YamtlGroovy.preloadMetamodel("/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/yamtl-comparator/src/main/resources/Ecore.ecore")
		def pk = resSM.contents.get(0) as EPackage

        System.out.println("Creating Instance");
		def matcher = new YamtlGroovy(pk)
		
        System.out.println("Loading Input models");
		matcher.loadInputModels([
            'model1': '/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/yamtl-comparator/src/main/resources/model_1.ecore', 
            'model2': '/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/yamtl-comparator/src/main/resources/model_2.ecore'])

        System.out.println("Executing Matcher");
		matcher.execute()
		
		matcher.saveOutputModels(['out': '/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/yamtl-comparator/src/main/resources/cd_output.ecore'])
    }
}