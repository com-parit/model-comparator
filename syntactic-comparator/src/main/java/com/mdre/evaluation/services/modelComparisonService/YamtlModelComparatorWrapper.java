package com.mdre.evaluation.services.modelComparisonService;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClass;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.File;

import groovy.lang.GroovyClassLoader;

public class YamtlModelComparatorWrapper {

    public static void main(String[] args) {
        System.out.println("hello");
		try {
			GroovyClassLoader loader = new GroovyClassLoader();
			Class<?> groovyClass = loader.parseClass(new File("src/main/java/com/mdre/evaluation/services/modelComparisonService/YamtlModelComparator.groovy"));

			java.lang.reflect.Method staticMethod = groovyClass.getMethod("run");
			staticMethod.invoke(null);
            
		} catch (Exception e) {
			System.out.println(e);
		}
    }
}