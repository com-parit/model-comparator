import json
import pandas as pd
import os
from visualizations import Visualizations as visualizations
import requests
from adapter import Adapter
from constants import CONSTANTS
from collections import OrderedDict

class Main:
    def reorder_model_level_df(self, model_level_df):
        desired_order_model_level = CONSTANTS.MODEL_LEVEL_COLUMNS_ORDER.value
        model_level_df = model_level_df.reindex(
            columns=desired_order_model_level)
        return model_level_df

    def reorder_class_level_df(self, class_level_df):
        desired_order_class_level = CONSTANTS.CLASS_LEVEL_ORDER.value
        class_level_df = class_level_df.reindex(
            columns=desired_order_class_level)
        return class_level_df

    def rename_model_level_columns(self, model_df):
        names = CONSTANTS.MODEL_LEVEL_RENAMING.value
        model_df = model_df.rename(columns=names)
        return model_df

    def rename_class_level_columns(self, class_level_df):
        names = CONSTANTS.CLASS_LEVEL_RENAMING.value
        class_level_df = class_level_df.rename(columns=names)
        return class_level_df

    def create_csv(self, model_level_json, class_level_json, output_dir):
        list_of_class_level_csv_paths = []
        list_of_model_level_csv_paths = []
        consolidated_csv_paths = []
        for project in sorted(list(class_level_json.keys())):
            df = pd.DataFrame.from_dict(
                class_level_json[project], orient='index')
            df = self.reorder_class_level_df(df)
            df = self.rename_class_level_columns(df)
            class_csv_path = f'{output_dir}/class-analysis.csv'
            df.to_csv(class_csv_path)
            list_of_class_level_csv_paths.append(class_csv_path)

            model_level_json[project]["model2_identifier"] = model_level_json[project]["model2_identifier"][model_level_json[project]
                                                                                                            ["model2_identifier"].rfind("/") + 1: len(model_level_json[project]["model2_identifier"])]
            model_level_json[project][
                "model1_identifier"] = f'{model_level_json[project]["model1_identifier"][model_level_json[project]["model1_identifier"].rfind("/") + 1: len(model_level_json[project]["model1_identifier"])]}'
            df_model = pd.DataFrame.from_dict(
                {"values": model_level_json[project]}, orient='index')
            df_model = self.reorder_model_level_df(df_model)
            df_model = self.rename_model_level_columns(df_model)
            model_csv_path = f'{output_dir}/model-analysis.csv'
            df_model.to_csv(model_csv_path)
            list_of_model_level_csv_paths.append(model_csv_path)
        paths_to_csvs = {
            "model": list_of_model_level_csv_paths,
            "class": list_of_class_level_csv_paths
        }

        # save consolidated csv
        df_model = pd.DataFrame.from_dict(model_level_json, orient='index')
        df_model = self.reorder_model_level_df(df_model)
        df_model = self.rename_model_level_columns(df_model)
        model_csv_path = f'{output_dir}/model-analysis-consolidated.csv'
        model_json_path = f'{output_dir}/model-analysis-consolidated.json'
        df_model.to_csv(model_csv_path)
        df_model.T.to_json(model_json_path)
        consolidated_csv_paths.append(model_csv_path)
        return consolidated_csv_paths

    def get_results_for_dataset(self):
        root_directory = "/media/jawad/secondaryStorage/projects/thesis/similar_models"
        for subfolder_name in os.listdir(root_directory):
            subfolder_path = os.path.join(root_directory, subfolder_name)
            if os.path.isdir(subfolder_path):
                model_1 = os.path.join(subfolder_path, "model_1.emf")
                model_2 = os.path.join(subfolder_path, "model_2.emf")
                projectName = subfolder_path
                config = "resources/config.json"
                groundTruthModel_emf = model_1
                predictedModel_emf = model_2

                output_dir = subfolder_path
                os.makedirs(output_dir, exist_ok=True)

                try:
                    model_level_json, class_level_json = Adapter.compare_emfatic_models_syntactically_and_semantically(
                        groundTruthModel_emf, predictedModel_emf, projectName, config)
                    
                    with open(f'{output_dir}/model_level_json.json', 'w', encoding='utf-8') as json_file:
                        json.dump(model_level_json, json_file, ensure_ascii=False, indent=4)
                    with open(f'{output_dir}/class_level_json.json', 'w', encoding='utf-8') as json_file:
                        json.dump(class_level_json, json_file, ensure_ascii=False, indent=4)
                    print(f'Computed for {output_dir}')
                except Exception as e:
                    print(e)

    def compute_similarity_for_test_set(self):
        root_directory = "/media/jawad/secondaryStorage/projects/thesis/evaluation/travis/evaluateTravis"
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
                        model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(
                            groundTruthModelEcore=ground_truth_ecore_model_file_path,
                            predictedModelEcore=predicted_ecore_model_file_path,
                            projectName=file,
                            config="resources/config.json"
                        )
                        with open(f'{sub_sub_folder}/model_level_json.json', 'w', encoding='utf-8') as json_file:
                            json.dump(model_level_json, json_file, ensure_ascii=False, indent=4)
                        with open(f'{sub_sub_folder}/class_level_json.json', 'w', encoding='utf-8') as json_file:
                            json.dump(class_level_json, json_file, ensure_ascii=False, indent=4)

    def run(self, ):
        projectName = "ecommerce-backend"
        config = "resources/config.json"
        groundTruthModel_emf = "ase2024-dataset/ecommerce-backend/modisco/ecommerce-modisco.emf"
        predictedModel_emf = "ase2024-dataset/ecommerce-backend/modisco/ecommerce-modisco-flat-without-errors.emf"
        
        groundTruthModelEcore = "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/yamtl-comparator/src/main/resources/bt_openlink/mutant_3/mutant_3.ecore" # "resources/btopenlinkjavacoremodel.ecore"
        predictedModelEcore = "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/yamtl-comparator/src/main/resources/bt_openlink/bt_openlink.ecore" # "resources/bt_openlink.ecore"

        output_dir = f'output'
        os.makedirs(output_dir, exist_ok=True)
        
        model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(
            groundTruthModelEcore, 
            predictedModelEcore,
            projectName,
            config
        )
        df_model = pd.DataFrame.from_dict(
            {"values": model_level_json[projectName]}, orient='index')
        df_model = self.reorder_model_level_df(df_model)
        df_model = self.rename_model_level_columns(df_model)
        json_result = df_model.to_json(orient='records', indent=4)
        print(json_result)
        with open(f'{output_dir}/model_level_json.json', 'w', encoding='utf-8') as json_file:
            json.dump(model_level_json, json_file, ensure_ascii=False, indent=4)
        with open(f'{output_dir}/class_level_json.json', 'w', encoding='utf-8') as json_file:
            json.dump(class_level_json, json_file, ensure_ascii=False, indent=4)

if __name__ == '__main__':
    Main().compute_similarity_for_test_set()
