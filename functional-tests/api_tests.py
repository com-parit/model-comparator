from adapter_ftests import Adapter_Ftests
import os
from report import create_report
import json
import pandas as pd
import shutil


def test_ecore_to_emfatic_conversion():
    print("===== Running tests for Ecore 2 Emfatic Conversion =====")
    try:
        emfatic_path = Adapter_Ftests.get_emfatic_from_ecore(
            "test_data/sample_ecore_model_base.ecore")
        print("Passed")
    except Exception as e:
        print("Failed")


def test_emfatic_to_ecore_conversion():
    print("===== Running tests for Emfatic 2 Ecore Conversion =====")
    try:
        ecore_path = Adapter_Ftests.get_ecore_model_from_emfatic(
            "test_data/sample_ecore_model_base.emf")
        print("Passed")
    except Exception as e:
        print("Failed")


def test_uml2_to_ecore_conversion():
    print("===== Running tests for Uml2 2 Ecore Conversion =====")
    try:
        ecore_path = Adapter_Ftests.get_ecore_model_from_uml2(
            "test_data/sample_uml_model_base.uml")
        print("Passed")
    except Exception as e:
        print("Failed")


def test_compare_ecore_models_syntactically_and_semantically(config_path, output_csv_path, copy_path):
    print("===== Running tests for ecore models syntactic and semantic comparison =====")
    if os.path.exists(copy_path):
        shutil.rmtree(copy_path)
    print("copying dataset for comparison")
    shutil.copytree("ecore_models_modelset", copy_path)
    root_directory = copy_path
    total_base_models = len(os.listdir(root_directory))
    count = 0
    processed_mutants = 0
    for subfolder_name in os.listdir(root_directory):
        subfolder_path = os.path.join(root_directory, subfolder_name)
        subfolder_path = os.path.join(subfolder_path, "base_model")
        is_big_model = False
        if os.path.isdir(subfolder_path):
            ground_truth_ecore_model_file_path = ""
            ground_truth_model_size = 0
            for file in os.listdir(subfolder_path):
                if ".ecore" in file:
                    count = count + 1
                    ground_truth_ecore_model_file_path = subfolder_path + "/" + file
                    ground_truth_model_size = os.path.getsize(
                        ground_truth_ecore_model_file_path) / (1024)
                    if ground_truth_model_size > 20:
                        is_big_model = True
                    else:
                        print(
                            f'Ignoring small model {ground_truth_model_size} KBs {ground_truth_ecore_model_file_path}')
            sub_folder_dir = os.listdir(subfolder_path)
            if is_big_model:
                print(
                    f'processing base model {count}/{total_base_models} files: {ground_truth_ecore_model_file_path}')
                print(f'comparing mutants')
                for file in sub_folder_dir:
                    predicted_ecore_model_file_path = ""
                    if os.path.isdir(os.path.join(subfolder_path, file)):
                        sub_sub_folder = os.path.join(subfolder_path, file)
                        for file2 in os.listdir(sub_sub_folder):
                            if ".ecore" in file2:
                                predicted_ecore_model_file_path = sub_sub_folder + "/" + file2
                        try:
                            model_level_json, class_level_json = Adapter_Ftests.compare_ecore_models_syntactically_and_semantically(
                                groundTruthModelEcore=ground_truth_ecore_model_file_path,
                                predictedModelEcore=predicted_ecore_model_file_path,
                                config=config_path
                            )
                            model_level_json["ground_truth_model_size"] = ground_truth_model_size
                            with open(f'{sub_sub_folder}/model_level_json.json', 'w', encoding='utf-8') as json_file:
                                json.dump(model_level_json, json_file,
                                          ensure_ascii=False, indent=4)
                            with open(f'{sub_sub_folder}/class_level_json.json', 'w', encoding='utf-8') as json_file:
                                json.dump(class_level_json, json_file,
                                          ensure_ascii=False, indent=4)
                            processed_mutants += 1
                        except Exception as e:
                            print(
                                f'Failed to process {predicted_ecore_model_file_path} with error {e}')

    print(f'base models processed: {count}')
    print(f'mutants processed: {processed_mutants}')
    df = create_report(root_directory)
    df.to_csv(output_csv_path)
    msqe_syntactic = (
        (df['expected_f1_score'] - df['comparit_f1_score']) ** 2).mean()
    msqe_semantic = ((df['expected_f1_score'] -
                     df['SEMANTIC_SIMILARITY']) ** 2).mean()
    print(f'Syntactic Similarity Mean Squared Error {msqe_syntactic}')
    print(f'Semantic Similarity Mean Squared Error {msqe_semantic}')


if __name__ == "__main__":
	user_input = input("This action will override existing test resutls. Do you wish to proceed? (Y/N)")
	if user_input == "Y":
		print("Executing Tests")
		test_ecore_to_emfatic_conversion()
		test_emfatic_to_ecore_conversion()
		test_uml2_to_ecore_conversion()
		# test_compare_ecore_models_syntactically_and_semantically("config_hashing_0.95_aggregate_true.json", "evaluation_results_hashing_0.95_aggregate_true.csv", "evaluate_config_hashing_0.95_aggregate_true")
		# test_compare_ecore_models_syntactically_and_semantically("config_hashing_0.95_aggregate_false.json", "evaluation_results_hashing_0.95_aggregate_false.csv", "evaluate_config_hashing_0.95_aggregate_false")
		# test_compare_ecore_models_syntactically_and_semantically("config_digest_aggregate_true.json", "evaluation_results_digest_aggregate_true.csv", "evaluate_config_digest_aggregate_true")
		# test_compare_ecore_models_syntactically_and_semantically("config_digest_aggregate_false.json", "evaluation_results_digest_aggregate_false.csv", "evaluate_config_digest_aggregate_false")
	else:
		print("Exiting")