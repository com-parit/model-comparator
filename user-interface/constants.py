
from enum import Enum


class CONSTANTS(Enum):
    SAMPLE_ECORE_MODEL_PAIRS = {
		"carAndBottle": [
			"""<?xml version="1.0" encoding="ASCII"?>
				<ecore:EPackage xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore" name="top" nsURI="top" nsPrefix="top">
				<eSubpackages name="default" nsURI="default" nsPrefix="">
					<eClassifiers xsi:type="ecore:EClass" name="car">
					<eStructuralFeatures xsi:type="ecore:EReference" name="wheels" containment="true">
						<eType xsi:type="ecore:EDataType" href="http://www.eclipse.org/emf/2002/Ecore#//EInt"/>
					</eStructuralFeatures>
					</eClassifiers>
				</eSubpackages>
				</ecore:EPackage>""",
			"""<?xml version="1.0" encoding="ASCII"?>
				<ecore:EPackage xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore" name="top" nsURI="top" nsPrefix="top">
				<eSubpackages name="default" nsURI="default" nsPrefix="">
					<eClassifiers xsi:type="ecore:EClass" name="bottle">
					<eStructuralFeatures xsi:type="ecore:EReference" name="caps" containment="true">
						<eType xsi:type="ecore:EDataType" href="http://www.eclipse.org/emf/2002/Ecore#//EInt"/>
					</eStructuralFeatures>
					</eClassifiers>
				</eSubpackages>
				</ecore:EPackage>"""			
		],
		"jmotiff": [
			open('samples/jmotiff_base.ecore', 'r').read(),
			open('samples/jmotiff_predicted.ecore', 'r').read()
		]
 	} 

    SAMPLE_EMFATIC_MODEL_PAIRS = {
		"carAndBottle": [
			"""@namespace(uri="top", prefix="top")
				package top;

				@namespace(uri="default", prefix="")
				package default {
					class car {
						val int wheels;
					}
				}""",
			"""@namespace(uri="top", prefix="top")
				package top;

				@namespace(uri="default", prefix="")
				package default {
					class bottle {
						val int caps;
					}

				}"""    
		],
		"jmotiff": [
			open('samples/jmotiff_base.emf', 'r').read(),
			open('samples/jmotiff_predicted.emf', 'r').read()
		]
 	} 
    
    SAMPLE_UML2_MODEL_PAIRS = {
		"sample": [
			open('samples/sample_uml_model.uml', 'r').read(),
			open('samples/sample_uml_model_predicted.uml', 'r').read()
		]
 	} 
    