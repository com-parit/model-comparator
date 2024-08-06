package com.mdre.evaluation.services.modelComparisonService;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClass;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.File;

import groovy.lang.GroovyClassLoader;

// import subtyping.EMFSubtyping;
// import yamtl.core.YAMTLModule;
// import static yamtl.dsl.Helper.*;
// import static yamtl.dsl.Rule.*;

public class YamtlComparator {

    // public YamtlComparator(EPackage CD) {
    //     header().in("model1", CD).in("model2", CD).out("out", CD);

    //     ruleStore(
    //         List.of(
    //             rule("MatchPackage")
    //             .in("p1", "model1", (EClass) CD.getEClassifier("Class"))
    //             .in("p2", "model2", (EClass) CD.getEClassifier("Class"))
    //             .filter(() -> p1().getName().equals(p2().getName()))
    //             .out("new_p", "out", (EClass) CD.getEClassifier("Class"), (p1, p2) -> {
    //                 // EObject new_p = EcoreUtil.copy(p1);
    //                 // new_p.eSet(new_p.eClass().getEStructuralFeature("name"), p1.getName());

    //                 // List<EObject> new_c_list = fetch(
    //                 //     Map.of("c1", p1.eGet(p1.eClass().getEStructuralFeature("classifiers")),
    //                 //            "c2", p2.eGet(p2.eClass().getEStructuralFeature("classifiers")))
    //                 // );

    //                 // new_p.eSet(new_p.eClass().getEStructuralFeature("classifiers"), new_c_list);
    //                 return new_p();
    //             }))
    //     );
    // }

    public static void main(String[] args) {
        System.out.println("hello");
		try {
			GroovyClassLoader loader = new GroovyClassLoader();
			Class<?> groovyClass = loader.parseClass(new File("src/main/java/com/mdre/evaluation/services/modelComparisonService/YamtlGroovy.groovy"));

			java.lang.reflect.Method staticMethod = groovyClass.getMethod("run");
			staticMethod.invoke(null);
            
			// Object groovyObject = groovyClass.newInstance();
			// java.lang.reflect.Method method = groovyClass.getMethod("run");

			// method.invoke(groovyObject);
			// System.out.println(result); // Outputs: Hello, World!
		} catch (Exception e) {
			System.out.println(e);
		}
        // EPackage pk = (EPackage) YamtlComparator.preloadMetamodel("/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/usage/resources/btopenlinkjavacoremodel.ecore").getContents().get(0);

        // // Create an instance of Comparator
        // YamtlComparator matcher = new YamtlComparator(pk);

        // // Load input models
        // matcher.loadInputModels(
        //     Map.of(
        //         "model1", "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/usage/resources/bt_openlink.ecore",
        //         "model2", "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/usage/resources/btopenlinkjavacoremodel.ecore"
        //     )
        // );

        // // Execute the comparison or transformation
        // matcher.execute();
		// System.out.println("Saving");

        // // Save output models
        // matcher.saveOutputModels(
        //     Map.of(
        //         "out", "cd_output.xmi"
        //     )
        // );
    }
}