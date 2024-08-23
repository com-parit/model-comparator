import os
import json
import pandas as pd

def create_report(directory):
	root_directory = directory
	results_json = {"base_model": [], "predicted_model": [], "expected_f1_score": [], "expected_precision": [], "expected_recall": [], "SMOTE": [], "comparit_precision": [], "comparit_recall": [], "comparit_f1_score": [], "SMOTE_ERROR": [], "COMPARIT_ERROR": []}
	for subfolder_name in os.listdir(root_directory):
		sub_dir = os.path.join(root_directory, subfolder_name)
		if os.path.isdir(sub_dir):
			for base_folder in os.listdir(sub_dir):
				if (os.path.isdir(os.path.join(sub_dir, base_folder))):
					sub_sub_folder_name = os.path.join(sub_dir, base_folder)
					for mutant in os.listdir(sub_sub_folder_name):
						if os.path.isdir(os.path.join(sub_sub_folder_name, mutant)):
							expected_precision = -1
							expected_recall = -1
							expected_f1_score = -1
							SMOTE = -1
							comparit_precision = -1
							comparit_recall = -1
							comparit_f1_score = -1
							try:
								with open(f'{os.path.join(sub_sub_folder_name, mutant)}/expected_results.json', 'r') as fr:
									expected_results = json.loads(fr.read())
									expected_precision = expected_results["results"]["aggregate_model_precision"]
									expected_recall = expected_results["results"]["aggregate_model_recall"]
									expected_f1_score = expected_results["results"]["aggregate_model_f1_score"]
								with open(f'{os.path.join(sub_sub_folder_name, mutant)}/model_level_json.json', 'r') as fr:
									actual_results = json.loads(fr.read())
									SMOTE = actual_results["semantic_similarity"]
									comparit_precision = actual_results["aggregate_model_precision"]
									comparit_recall = actual_results["aggregate_model_recall"]
									comparit_f1_score = actual_results["aggregate_model_f1_score"]
							except Exception as e:
								print(e)
							results_json["base_model"].append(os.path.join(sub_sub_folder_name, "mutant_1"))
							results_json["predicted_model"].append(os.path.join(sub_sub_folder_name, mutant))
							results_json["expected_precision"].append(expected_precision)
							results_json["expected_recall"].append(expected_recall)
							results_json["expected_f1_score"].append(expected_f1_score)
							results_json["comparit_precision"].append(comparit_precision)
							results_json["comparit_recall"].append(comparit_recall)
							results_json["comparit_f1_score"].append(comparit_f1_score)
							results_json["SMOTE"].append(SMOTE)
							results_json["SMOTE_ERROR"].append(round(abs(SMOTE - expected_f1_score), 5))
							results_json["COMPARIT_ERROR"].append(round((abs(comparit_f1_score - expected_f1_score)), 3))
		
	df = pd.DataFrame(results_json)
	return df