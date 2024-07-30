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

import java.util.ArrayList;
import java.util.HashMap;

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
import com.mdre.evaluation.dtos.VenDiagramClassesDTO;
import com.mdre.evaluation.dtos.VenDiagramEEnumsDTO;
import com.mdre.evaluation.dtos.VenDiagramEAttributesDTO;
import com.mdre.evaluation.dtos.VenDiagramEReferencesDTO;
import com.mdre.evaluation.dtos.VenDiagramEOperationsDTO;

abstract class AbstractClassComparisonService {
	public abstract VenDiagramClassesDTO getVenDiagramForClasses(List<EClass> classesModel1, List<EClass> classesModel2);
	public abstract VenDiagramEEnumsDTO getVenDiagramForEnumerations(List<EEnum> enumsModel1, List<EEnum> enumsModel2);
	public abstract VenDiagramEAttributesDTO getVenDiagramForEAttributes(List<EAttribute> attributesClass1, List<EAttribute> attributesClass2);
	public abstract VenDiagramEReferencesDTO getVenDiagramForEReferences(List<EReference> ereferencesClass1, List<EReference> ereferencesClass2);
	public abstract VenDiagramEOperationsDTO getVenDiagramForEOperations(List<EOperation> eoperationsClass1, List<EOperation> eoperationsClass2);
    public abstract HashMap<String, Object> computeSimilarity(ArrayList<HashMap<String, String>> originalDigest, ArrayList<HashMap<String, String>> predictedDigest);
}