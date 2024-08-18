import json
import pandas as pd
import os
from adapter import Adapter
from constants import CONSTANTS
from report import create_report

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

    def compute_similarity_for_test_set(self):
        root_directory = "evaluate_travis"
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
        df.to_csv(f'{root_directory}/results.csv')


    def run(self, ):
        config = "config.json"
        groundTruthModel_emf = "ase2024-dataset/ecommerce-backend/modisco/ecommerce-modisco.emf"
        predictedModel_emf = "ase2024-dataset/ecommerce-backend/modisco/ecommerce-modisco-flat-without-errors.emf"
        
        groundTruthModelEcore = "/media/jawad/secondaryStorage/leicester/uol/thesis/repo/jm982/code/branches/model-comparator-main/usage/ase2024-dataset/ecommerce-backend/ground-truth/ecommerce2.ecore"
        predictedModelEcore = "bt_openlink.ecore"

        output_dir = f'output'
        os.makedirs(output_dir, exist_ok=True)
        
        model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(
            groundTruthModelEcore, 
            predictedModelEcore,
            config
        )
        print(json.dumps(model_level_json, indent=4))
        with open(f'{output_dir}/model_level_json.json', 'w', encoding='utf-8') as json_file:
            json.dump(model_level_json, json_file, ensure_ascii=False, indent=4)
        with open(f'{output_dir}/class_level_json.json', 'w', encoding='utf-8') as json_file:
            json.dump(class_level_json, json_file, ensure_ascii=False, indent=4)

if __name__ == '__main__':
    Main().compute_similarity_for_test_set()
