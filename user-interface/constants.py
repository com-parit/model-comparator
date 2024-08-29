
from enum import Enum


class CONSTANTS(Enum):
    SAMPLE_ECORE_MODEL_PAIRS = {
		"sample": [
			open('samples/sample_ecore_model_base.ecore', 'r').read(),
			open('samples/sample_ecore_model_predicted.ecore', 'r').read()
		]
 	} 

    SAMPLE_EMFATIC_MODEL_PAIRS = {
		"sample": [
			open('samples/sample_ecore_model_base.emf', 'r').read(),
			open('samples/sample_ecore_model_predicted.emf', 'r').read()
		]
 	} 
    
    SAMPLE_UML2_MODEL_PAIRS = {
		"sample": [
			open('samples/sample_uml_model_base.uml', 'r').read(),
			open('samples/sample_uml_model_predicted.uml', 'r').read()
		]
 	} 
    
    SAMPLE_ECORE_MODEL_SINGLE = open('samples/sample_ecore_model_base.ecore', 'r').read()
    SAMPLE_EMFATIC_MODEL_SINGLE = open('samples/sample_ecore_model_base.emf', 'r').read()
    SAMPLE_UML2_MODEL_SINGLE = open('samples/sample_uml_model_base.uml', 'r').read()
    