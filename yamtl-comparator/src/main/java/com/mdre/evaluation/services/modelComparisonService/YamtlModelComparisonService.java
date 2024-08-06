// package com.mdre.evaluation.services.modelComparisonService;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.Comparator;
// import java.io.FileWriter;
// import java.io.IOException;
// import org.json.*;
// import java.io.FileNotFoundException;
// import java.io.File;

// import java.util.ArrayList;
// import java.util.HashMap;

// import org.eclipse.emf.common.util.EList;
// import org.eclipse.emf.common.util.URI;
// import org.eclipse.emf.ecore.EOperation;
// import org.eclipse.emf.ecore.EAttribute;
// import org.eclipse.emf.ecore.EReference;
// import org.eclipse.emf.ecore.EClass;
// import org.eclipse.emf.ecore.EEnum;
// import org.eclipse.emf.ecore.EObject;
// import org.eclipse.emf.ecore.EPackage;
// import org.eclipse.emf.ecore.EParameter;
// import org.eclipse.emf.ecore.EAnnotation;
// import com.mdre.evaluation.dtos.VenDiagramDTO;
// import com.mdre.evaluation.dtos.MatchedElementsDTO;
// import com.mdre.evaluation.services.modelComparisonService.YamtlComparator;


// public class YamtlModelComparisonService extends AbstractClassComparisonService {

// 	public YamtlModelComparisonService() {
// 		System.out.println("Initializing YAmtl");
//         // Preload the metamodel
//         EPackage pk = (EPackage) YamtlComparator.preloadMetamodel("/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/usage/resources/btopenlinkjavacoremodel.ecore").getContents().get(0);

//         // Create an instance of Comparator
//         YamtlComparator matcher = new YamtlComparator(pk);

//         // Load input models
//         matcher.loadInputModels(
//             Map.of(
//                 "model1", "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/usage/resources/btopenlinkjavacoremodel.ecore",
//                 "model2", "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/usage/resources/btopenlinkjavacoremodel.ecore"
//             )
//         );

//         // Execute the comparison or transformation
//         matcher.execute();
// 		System.out.println("Saving");

//         // Save output models
//         matcher.saveOutputModels(
//             Map.of(
//                 "out", "cd_output.xmi"
//             )
//         );
// 	}

// 	public double computeSimilarity(Object comparisonObject1, Object comparisonObject2) {
// 		return 1.0;
// 	}

// 	public VenDiagramDTO<EClass>  getVenDiagramForClasses(List<EClass> classesModel1, List<EClass> classesModel2) {
// 		// ClassComparator matcher = new ClassComparator();
// 		VenDiagramDTO<EClass> result = new VenDiagramDTO<EClass>();
// 		// List<EClass> classes = matcher.fetch(Map.of("model1Classes", classesModel1, "model2Classes", classesModel2));
// 		// System.out.println(classes);
// 		// result.matched = classes;
// 		return result;
// 	}

// 	public VenDiagramDTO<EEnum> getVenDiagramForEnumerations(List<EEnum> enumsModel1, List<EEnum> enumsModel2){
// 		VenDiagramDTO<EEnum> result = new VenDiagramDTO<EEnum>();
// 		return result;
// 	}
	
// 	public VenDiagramDTO<EAttribute> getVenDiagramForEAttributes(List<EAttribute> attributesClass1, List<EAttribute> attributesClass2){
// 		VenDiagramDTO<EAttribute> result = new VenDiagramDTO<EAttribute>();
// 		return result;
// 	}
	
// 	public VenDiagramDTO<EReference> getVenDiagramForEReferences(List<EReference> ereferencesClass1, List<EReference> ereferencesClass2){
// 		VenDiagramDTO<EReference> result = new VenDiagramDTO<EReference>();
// 		return result;
// 	}
	
// 	public VenDiagramDTO<EOperation> getVenDiagramForEOperations(List<EOperation> eoperationsClass1, List<EOperation> eoperationsClass2){
// 		VenDiagramDTO<EOperation> result = new VenDiagramDTO<EOperation>();
// 		return result;
// 	}
	
// }