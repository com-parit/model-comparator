import os
import json
import pandas as pd


def get_json_file_as_dict(path):
    try:
        with open(path, 'r') as fr:
            return json.loads(fr.read())
    except Exception as e:
        return {}

def create_report(directory):
	root_directory = directory
	results_json = {
		"base_model": [],
		"predicted_model": [],
		"expected_f1_score": [],
		"expected_precision": [],
		"expected_recall": [],
		"SEMANTIC_SIMILARITY": [],
		"comparit_precision": [],
		"comparit_recall": [],
		"comparit_f1_score": [],
		"SEMANTIC_SIMILARITY_ERROR": [],
		"COMPARIT_ERROR": []
	}

	for subfolder_name in os.listdir(root_directory):
		sub_dir = os.path.join(root_directory, subfolder_name)
		if os.path.isdir(sub_dir):
			for mutants_folder in os.listdir(sub_dir):
				if (os.path.isdir(os.path.join(sub_dir, mutants_folder))):
					individual_mutant_folder = os.path.join(sub_dir, mutants_folder)
					for file_in_mutant_folder in os.listdir(individual_mutant_folder):
						if os.path.isdir(os.path.join(individual_mutant_folder, file_in_mutant_folder)):
							expected_results = get_json_file_as_dict(
							f'{os.path.join(individual_mutant_folder, file_in_mutant_folder)}/expected_results.json')
							model_level_json = get_json_file_as_dict(
							f'{os.path.join(individual_mutant_folder, file_in_mutant_folder)}/model_level_json.json')
							class_level_json = get_json_file_as_dict(
							f'{os.path.join(individual_mutant_folder, file_in_mutant_folder)}/class_level_json.json')

							try:
								results_json["base_model"].append(individual_mutant_folder[:individual_mutant_folder.rindex("/")])
							except KeyError as ke:
								results_json["base_model"].append("")

							try:
								results_json["predicted_model"].append(os.path.join(
							individual_mutant_folder, file_in_mutant_folder))
							except KeyError as ke:
								results_json["predicted_model"].append("")

							try:
								results_json["expected_precision"].append(
							expected_results["results"]["aggregate_model_precision"])
							except KeyError as ke:
								results_json["expected_precision"].append(-1)

							try:
								results_json["expected_recall"].append(
							expected_results["results"]["aggregate_model_recall"])
							except KeyError as ke:
								results_json["expected_recall"].append(-1)

							try:
								results_json["expected_f1_score"].append(expected_results["results"]["aggregate_model_f1_score"])
							except KeyError as e:
								results_json["expected_f1_score"].append(-1)

							try:
								results_json["comparit_precision"].append(model_level_json["aggregate_model_precision"])
							except KeyError as ke:
								results_json["comparit_precision"].append(-1)

							try:
								results_json["comparit_recall"].append(model_level_json["aggregate_model_recall"])
							except KeyError as ke:
								results_json["comparit_recall"].append(-1)

							try:
								results_json["comparit_f1_score"].append(model_level_json["aggregate_model_f1_score"])
							except KeyError as ke:
								results_json["comparit_f1_score"].append(-1)

							try:
								results_json["SEMANTIC_SIMILARITY"].append(model_level_json["semantic_similarity"])
							except KeyError as ke:
								results_json["SEMANTIC_SIMILARITY"].append(-1)

							try:
								results_json["SEMANTIC_SIMILARITY_ERROR"].append(round(abs(model_level_json["semantic_similarity"] - expected_results["results"]["aggregate_model_f1_score"]), 5))
							except KeyError as ke:
								results_json["SEMANTIC_SIMILARITY_ERROR"].append(-1)

							try:
								results_json["COMPARIT_ERROR"].append(round((abs(model_level_json["aggregate_model_f1_score"] - expected_results["results"]["aggregate_model_f1_score"])), 3))
							except KeyError as ke:
								results_json["COMPARIT_ERROR"].append(-1)

	df = pd.DataFrame(results_json)
	return df
