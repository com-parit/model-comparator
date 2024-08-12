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

import org.eclipse.emf.ecore.EcorePackage;

import subtyping.EMFSubtyping;
import yamtl.core.YAMTLModule;

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

public class MetricsComputationService {
    public static HashMap<String, Float> computeMetrics(HashMap<String, Integer> metrics) {
        Float tp = (float) metrics.getOrDefault("tp", 0);
        Float fp = (float) metrics.getOrDefault("fp", 0);
        Float fn = (float) metrics.getOrDefault("fn", 0);

        Float precision = computePrecision(tp, fp);
        Float recall = computeRecall(tp, fn);
        Float f1Score = computeF1Score(precision, recall);

		HashMap<String, Float> returnValue = new HashMap<String, Float>();
		returnValue.put("precision", precision);
		returnValue.put("recall", recall);
		returnValue.put("f1Score", f1Score);
        return returnValue;
    }

    public static Float computePrecision(Float tp, Float fp) {
        if (tp + fp == 0) {
            return (float) 0;
        }
        return (Float) tp / (tp + fp);
    }

    public static Float computeRecall(float tp, float fn) {
        if (tp + fn == 0) {
            return (float) 0;
        }
        return tp / (tp + fn);
    }

    public static Float computeF1Score(float precision, float recall) {
        if (precision + recall == 0) {
            return (float) 0;
        }
        return 2 * (precision * recall) / (precision + recall);
    }	
}