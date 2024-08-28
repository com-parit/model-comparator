// /*******************************************************************************
//  * Copyright (c) 2006, 2009 Obeo.
//  * All rights reserved. This program and the accompanying materials
//  * are made available under the terms of the Eclipse Public License v1.0
//  * which accompanies this distribution, and is available at
//  * http://www.eclipse.org/legal/epl-v10.html
//  * 
//  * Contributors:
//  *     Obeo - initial API and implementation
//  *******************************************************************************/
// package com.mdre.evaluation.services.modelComparisonService;

// import java.io.File;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Calendar;
// import java.util.Collections;
// import java.util.List;
// import org.eclipse.emf.compare.match.service.*;
// import org.eclipse.emf.ecore.resource.ResourceSet;
// import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
// import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
// import org.eclipse.emf.compare.util.ModelUtils;
// import org.eclipse.emf.ecore.EObject;
// import org.eclipse.emf.ecore.EClass;
// import org.eclipse.emf.compare.match.metamodel.*;
// import org.eclipse.emf.compare.diff.metamodel.*;
// import org.eclipse.emf.compare.diff.service.*;
// import org.eclipse.emf.compare.diff.merge.service.*;
// import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
// import org.eclipse.emf.common.util.BasicMonitor;
// import org.eclipse.core.runtime.Path;
// import org.eclipse.emf.common.util.URI;
// import org.eclipse.emf.ecore.resource.Resource;
// // import org.eclipse.emf.compare.match.metamodel.UnMatchElement;
// /**
//  * This application will try and launch an headless model comparison.
//  * 
//  * @author Cedric Brun <a href="mailto:cedric.brun@obeo.fr">cedric.brun@obeo.fr</a>
//  */
// public final class EMFCompareService {
// 	/**
// 	 * This class doesn't need to be instantiated.
// 	 */
// 	private EMFCompareService() {
// 		// prevents instantiation
// 	}

// 	private static Integer findTruePositives(ArrayList<MatchElement> matchedElements) {
// 		Integer truePositives = 0;
// 		for(MatchElement mel: matchedElements) {

// 			Match2Elements ele = (Match2Elements) mel;
// 			System.out.println("");
// 			System.out.println(ele.getLeftElement());
// 			System.out.println(ele.getRightElement());
// 			System.out.println(mel.getSimilarity());
// 			// System.out.println(ele.getLeftElement());
// 			// System.out.println(ele.eContents());
// 			if (ele.getLeftElement() instanceof EClass) {
// 				EClass eclass = (EClass) ele.getLeftElement();
// 				Integer noOfAttributes = eclass.getEAttributes().size();
// 				Integer noOfOperations = eclass.getEOperations().size();
// 				Integer noOfReferences = eclass.getEReferences().size();
// 				int finalValue = (int) Math.round((noOfAttributes + noOfOperations + noOfReferences) * mel.getSimilarity());
// 				System.out.println(finalValue);
// 				truePositives = truePositives + 1 + finalValue; 
// 			}
// 			System.out.println("");
// 			ArrayList<MatchElement> subElements = new ArrayList<MatchElement>(mel.getSubMatchElements());
// 			if (subElements.size() > 0) {
// 				truePositives = truePositives + findTruePositives(subElements);
// 			}
// 		}
// 		return truePositives;
// 	}

// 	private static Integer findFalsePositives(ArrayList<UnmatchElement> matchedElements) {
// 		Integer falsePositives = 0;
// 		for(UnmatchElement mel: matchedElements) {
// 			System.out.println(mel.getElement());

// 			falsePositives = falsePositives + 1;
// 			// ArrayList<UnmatchElement> subElements = new ArrayList<UnmatchElement>(mel.getSubMatchElements());
// 			// if (subElements.size() > 0) {
// 			// 	truePositives = truePositives + findTruePositives(subElements);
// 			// }
// 		}
// 		return falsePositives;
// 	}


// 	/**
// 	 * Launcher of this application.
// 	 * 
// 	 * @param args
// 	 *            Arguments of the launch.
// 	 */
// 	public static void main(String[] args) {
// 		// Creates the resourceSets where we'll load the models
// 		final ResourceSet resourceSet1 = new ResourceSetImpl();
// 		final ResourceSet resourceSet2 = new ResourceSetImpl();
// 		// Register additionnal packages here. For UML2 for instance :
// 		// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION,
// 		// UMLResource.Factory.INSTANCE);
// 		// resourceSet1.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
// 		// resourceSet2.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);

// 		try {
// 			String model1_file_path = "/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/functional-tests/evaluate_travis/tracee_binding_springhttpclient/base_model/mutant_1/mutant_1.ecore";
// 			String model2_file_path = "/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/functional-tests/evaluate_travis/tracee_binding_springhttpclient/base_model/mutant_2/mutant_2.ecore";// "/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/functional-tests/evaluate_travis/steve/base_model/mutant_3/mutant_3.ecore";
// 			resourceSet1.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
// 			Resource ecoreResource = resourceSet1.getResource(org.eclipse.emf.common.util.URI.createFileURI(model1_file_path), true);
// 			ecoreResource.setURI(URI.createFileURI(model1_file_path));

// 			resourceSet2.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
// 			Resource ecoreResource2 = resourceSet2.getResource(org.eclipse.emf.common.util.URI.createFileURI(model2_file_path), true);
// 			ecoreResource2.setURI(URI.createFileURI(model2_file_path));

// 			System.out.println("Loading resources.\n"); //$NON-NLS-1$
// 			// Loads the two models passed as arguments
// 			final EObject model1 = ModelUtils.load(new File(model1_file_path), resourceSet1);
// 			final EObject model2 = ModelUtils.load(new File(model2_file_path), resourceSet2);

// 			// Creates the match then the diff model for those two models
// 			System.out.println("Matching models.\n"); //$NON-NLS-1$
// 			final MatchModel match = MatchService.doMatch(model1, model2, Collections
// 					.<String, Object> emptyMap());
// 			final ArrayList<MatchElement> matchedElements = new ArrayList<MatchElement>(match.getMatchedElements());
// 			Integer truePositives = findTruePositives(matchedElements);
// 			System.out.println(truePositives);

// 			final ArrayList<UnmatchElement> unmatchedElements = new ArrayList<UnmatchElement>(match.getUnmatchedElements());
// 			Integer falsePositives = findFalsePositives(unmatchedElements);
// 			System.out.println(falsePositives);
// 			// System.out.println(matchedElements);
// 			// for(MatchElement mel: matchedElements) {
// 			// 	System.out.println(mel.getSubMatchElements()); //$NON-NLS-1$
// 			// }

// 			// System.out.println("Differencing models.\n"); //$NON-NLS-1$
// 			// for (MatchElement mel : match.getMatchedElements()) {
// 			// 	System.out.println(mel.getSubMatchElements()); //$NON-NLS-1$
// 			// }
// 			// System.out.println("Unmatched elements.\n"); //$NON-NLS-1$
// 			// for (UnmatchElement mel : match.getUnmatchedElements()) {
// 			// 	System.out.println(mel.getElement().eContents()); //$NON-NLS-1$
// 			// }
// 			// System.out.println("===================================\n"); //$NON-NLS-1$
// 			// final DiffModel diff = DiffService.doDiff(match, false);
// 			// System.out.println(diff.getLeftRoots());
// 			// System.out.println("Differences");
// 			// final List<DiffElement> differences = new ArrayList<DiffElement>(diff.getOwnedElements());
// 			// System.out.println(differences);
// 			// for(DiffElement mel: differences) {
// 			// 	try {
// 			// 		// System.out.println(mel.eContents); //$NON-NLS-1$
// 			// 	} catch (Exception e) {
// 			// 		System.out.println(e);
// 			// 	}
// 			// }

// 			// System.out.println("End");
// 			// System.out.println("Merging difference to args[1].\n"); //$NON-NLS-1$
// 			// final List<DiffElement> differences = new ArrayList<DiffElement>(diff.getOwnedElements());
// 			// System.out.println(differences);
// 			// // This will merge all references to the right model (second argument).
// 			// MergeService.merge(differences, true);

// 			// Prints the results
// 			// try {
// 			// 	System.out.println("MatchModel :\n"); //$NON-NLS-1$
// 			// 	ModelUtils.serialize(match);
// 			// 	System.out.println("DiffModel :\n"); //$NON-NLS-1$
// 			// 	ModelUtils.serialize(diff);
// 			// } catch (final IOException e) {
// 			// 	e.printStackTrace();
// 			// }

// 			// // Serializes the result as "result.emfdiff" // in the directory this class has been called from.
// 			// System.out.println("saving emfdiff as \"result.emfdiff\""); //$NON-NLS-1$
// 			// final ComparisonResourceSnapshot snapshot = DiffFactory.eINSTANCE
// 			// 		.createComparisonResourceSnapshot();
// 			// snapshot.setDate(Calendar.getInstance().getTime());
// 			// snapshot.setMatch(match);
// 			// snapshot.setDiff(diff);
// 			// ModelUtils.save(snapshot, "result.emfdiff"); //$NON-NLS-1$
// 		} catch (final IOException e) {
// 			// shouldn't be thrown
// 			e.printStackTrace();
// 		} catch (final InterruptedException e) {
// 			e.printStackTrace();
// 		}
// 	}
// }