from adapter import Adapter
import os
from constants import CONSTANTS
from report import create_report
import json
import pandas as pd

def test_ecore_to_emfatic_conversion():
    print("===== Running tests for Ecore 2 Emfatic Conversion =====")
    try:
        emfatic_path = Adapter.get_emfatic_from_ecore("test_data/sample_ecore_model_base.ecore")
        print("Passed")
    except Exception as e:
        print("Failed")


def test_emfatic_to_ecore_conversion():
    print("===== Running tests for Emfatic 2 Ecore Conversion =====")
    try:
        ecore_path = Adapter.get_ecore_model_from_emfatic("test_data/sample_ecore_model_base.emf")
        print("Passed")
    except Exception as e:
        print("Failed")


def test_uml2_to_ecore_conversion():
    print("===== Running tests for Uml2 2 Ecore Conversion =====")
    try:
        ecore_path = Adapter.get_ecore_model_from_uml2("test_data/sample_uml_model_base.uml")
        print("Passed")
    except Exception as e:
        print("Failed")


def test_compare_ecore_models_syntactically_and_semantically():
	print("===== Running tests for ecore models syntactic and semantic comparison =====")
	root_directory = "/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/user-interface/evaluate_travis"
	for subfolder_name in os.listdir(root_directory):
		subfolder_path = os.path.join(root_directory, subfolder_name)
		subfolder_path = os.path.join(subfolder_path, "base_model")
		if os.path.isdir(subfolder_path):
			ground_truth_ecore_model_file_path = ""
			for file in os.listdir(subfolder_path):
				if ".ecore" in file:
					ground_truth_ecore_model_file_path = subfolder_path + "/" + file
			for file in os.listdir(subfolder_path):
				predicted_ecore_model_file_path = ""
				if os.path.isdir(os.path.join(subfolder_path, file)):
					sub_sub_folder = os.path.join(subfolder_path, file)
					for file2 in os.listdir(sub_sub_folder):
						if ".ecore" in file2:
							predicted_ecore_model_file_path = sub_sub_folder + "/" + file2
					print(f'processing {predicted_ecore_model_file_path}')
					model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(
						groundTruthModelEcore=ground_truth_ecore_model_file_path,
						predictedModelEcore=predicted_ecore_model_file_path,
						config="config.json"
					)
					with open(f'{sub_sub_folder}/model_level_json.json', 'w', encoding='utf-8') as json_file:
						json.dump(model_level_json, json_file, ensure_ascii=False, indent=4)
					with open(f'{sub_sub_folder}/class_level_json.json', 'w', encoding='utf-8') as json_file:
						json.dump(class_level_json, json_file, ensure_ascii=False, indent=4)
	df = create_report(root_directory)
	df.to_csv("/mnt/mydrive/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/user-interface/evaluation_results.csv")
	msqe_syntactic = ((df['expected_f1_score'] - df['comparit_f1_score']) ** 2).mean()
	msqe_semantic = ((df['expected_f1_score'] - df['SEMANTIC_SIMILARITY']) ** 2).mean()
	print(f'Syntactic Similarity Mean Squared Error {msqe_syntactic}')
	print(f'Semantic Similarity Mean Squared Error {msqe_semantic}')
    
# test_ecore_to_emfatic_conversion()
# test_emfatic_to_ecore_conversion()
# test_uml2_to_ecore_conversion()
test_compare_ecore_models_syntactically_and_semantically()
