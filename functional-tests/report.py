import os
import json
import pandas as pd


def get_json_file_as_dict(path):
    with open(path, 'r') as fr:
        return json.loads(fr.read())


def create_report(directory):
    root_directory = directory
    results_json_array = []
    # {
    #     "base_model": [],
    #     "predicted_model": [],
    #     "expected_f1_score": [],
    #     "expected_precision": [],
    #     "expected_recall": [],
    #     "SEMANTIC_SIMILARITY": [],
    #     "comparit_precision": [],
    #     "comparit_recall": [],
    #     "comparit_f1_score": [],
    #     "SEMANTIC_SIMILARITY_ERROR": [],
    #     "COMPARIT_ERROR": []
    # }

    included = 0
    excluded = 0
    for subfolder_name in os.listdir(root_directory):
        sub_dir = os.path.join(root_directory, subfolder_name)
        if os.path.isdir(sub_dir):
            for mutants_folder in os.listdir(sub_dir):
                if (os.path.isdir(os.path.join(sub_dir, mutants_folder))):
                    individual_mutant_folder = os.path.join(
                        sub_dir, mutants_folder)
                    for file_in_mutant_folder in os.listdir(individual_mutant_folder):
                        if os.path.isdir(os.path.join(individual_mutant_folder, file_in_mutant_folder)):
                            try:
                                expected_results = get_json_file_as_dict(
                                    f'{os.path.join(individual_mutant_folder, file_in_mutant_folder)}/expected_results.json')
                                model_level_json = get_json_file_as_dict(
                                    f'{os.path.join(individual_mutant_folder, file_in_mutant_folder)}/model_level_json.json')
                                class_level_json = get_json_file_as_dict(
                                    f'{os.path.join(individual_mutant_folder, file_in_mutant_folder)}/class_level_json.json')

                                record = {**model_level_json}
                                try:
                                    record["base_model"] = individual_mutant_folder[:individual_mutant_folder.rindex(
                                        "/")]
                                except KeyError as ke:
                                    record["base_model"] = ""

                                try:
                                    record["predicted_model"] = os.path.join(
                                        individual_mutant_folder, file_in_mutant_folder)
                                except KeyError as ke:
                                    record["predicted_model"] = ""

                                try:
                                    record["expected_precision"] = expected_results["results"]["aggregate_model_precision"]
                                except KeyError as ke:
                                    record["expected_precision"] = -1

                                try:
                                    record["expected_recall"] = expected_results["results"]["aggregate_model_recall"]
                                except KeyError as ke:
                                    record["expected_recall"] = -1

                                try:
                                    record["expected_f1_score"] = expected_results["results"]["aggregate_model_f1_score"]
                                except KeyError as e:
                                    record["expected_f1_score"] = -1

                                try:
                                    record["comparit_precision"] = model_level_json["aggregate_model_precision"]
                                except KeyError as ke:
                                    record["comparit_precision"] = -1

                                try:
                                    record["comparit_recall"] = model_level_json["aggregate_model_recall"]
                                except KeyError as ke:
                                    record["comparit_recall"] = -1

                                try:
                                    record["comparit_f1_score"] = model_level_json["aggregate_model_f1_score"]
                                except KeyError as ke:
                                    record["comparit_f1_score"] = -1

                                try:
                                    record["SEMANTIC_SIMILARITY"] = model_level_json["semantic_similarity"]
                                except KeyError as ke:
                                    record["SEMANTIC_SIMILARITY"] = -1

                                try:
                                    record["SEMANTIC_SIMILARITY_ERROR"] = round(abs(
                                        model_level_json["semantic_similarity"] - expected_results["results"]["aggregate_model_f1_score"]), 5)
                                except KeyError as ke:
                                    record["SEMANTIC_SIMILARITY_ERROR"] = -1

                                try:
                                    record["COMPARIT_ERROR"] = round(
                                        (abs(model_level_json["aggregate_model_f1_score"] - expected_results["results"]["aggregate_model_f1_score"])), 3)
                                except KeyError as ke:
                                    record["COMPARIT_ERROR"] = -1
                                results_json_array.append(record)
                                included += 1
                            except Exception as e:
                                excluded += 1

    df = pd.DataFrame(results_json_array)
    print(f'included: {included}')
    print(f'excluded: {excluded}')
    return df


if __name__ == "__main__":
    print("generating report for evaluate_config_digest_aggregate_true")
    df = create_report("evaluate_config_digest_aggregate_true")
    df.to_csv("evaluation_results_digest_aggregate_true.csv")

    print("generating report for evaluate_config_digest_aggregate_false")
    df = create_report("evaluate_config_digest_aggregate_false")
    df.to_csv("evaluation_results_digest_aggregate_false.csv")

    print("generating report for evaluate_config_hashing_0_95_aggregate_false")
    df = create_report("evaluate_config_hashing_0.95_aggregate_false")
    df.to_csv("evaluation_results_hashing_0.95_aggregate_false.csv")

    print("generating report for evaluate_config_hashing_0_95_aggregate_true")
    df = create_report("evaluate_config_hashing_0.95_aggregate_true")
    df.to_csv("evaluation_results_hashing_0.95_aggregate_true.csv")

    print("generating report for evaluate_config_hashing_1_aggregate_true")
    df = create_report("evaluate_config_hashing_1_aggregate_true")
    df.to_csv("evaluation_results_hashing_1_aggregate_true.csv")

    print("generating report for evaluate_config_hashing_1_aggregate_false")
    df = create_report("evaluate_config_hashing_1_aggregate_false")
    df.to_csv("evaluation_results_hashing_1_aggregate_false.csv")
