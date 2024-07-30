import json
import pandas as pd
import os
from visualizations import Visualizations as visualizations
import requests
from adapter import Adapter
class Main:
    def reorder_model_level_df(self, model_level_df):
        desired_order_model_level = [
            'model1_identifier', 
            'model2_identifier', 
            'total_classes_model1', 
            'total_classes_model2', 
            'total_classes_diff_model1_minus_model2', 
            'total_classes_diff_model2_minus_model1', 
            'classes_tp', 
            'classes_fp', 
            'classes_fn', 
            'total_attributes_model1', 
            'total_attributes_model2', 
            'attributes_tp', 
            'attributes_fn', 
            'attributes_fp', 
            'total_attributes_diff_model1_minus_model2', 
            'total_attributes_diff_model2_minus_model1', 
            'total_operations_model1', 
            'total_operations_model2', 
            'total_operations_diff_model1_minus_model2', 
            'total_operations_diff_model2_minus_model1', 
            'operations_tp', 
            'operations_fn',
            'operations_fp', 
            # 'total_regular_references_model1', 
            # 'total_regular_references_model2', 
            # 'total_regular_references_diff_model2_minus_model1', 
            # 'total_regular_references_diff_model1_minus_model2', 
            # 'regular_references_tp', 
            # 'regular_references_fp', 
            # 'regular_references_fn', 
            # 'total_containments_model1', 
            # 'total_containments_model2', 
            # 'total_containments_diff_model1_minus_model2', 
            # 'total_containments_diff_model2_minus_model1', 
            # 'containments_tp', 
            # 'containments_fp', 
            # 'containments_fn', 
            'total_references_model1', 
            'total_references_model2', 
            'total_references_diff_model1_minus_model2', 
            'total_references_diff_model2_minus_model1',
            'references_tp', 
            'references_fp', 
            'references_fn', 
            'total_enumerations_model1', 
            'total_enumerations_model2', 
            'total_enumerations_diff_model1_minus_model2', 
            'total_enumerations_diff_model2_minus_model1', 
            'enumerations_tp', 
            'enumerations_fp', 
            'enumerations_fn', 
            'total_superTypes_model1', 
            'total_superTypes_model2', 
            'total_superTypes_diff_model1_minus_model2', 
            'total_superTypes_diff_model2_minus_model1', 
            'superTypes_tp', 
            'superTypes_fp', 
            'superTypes_fn', 
            'aggregate_model_precision', 
            'aggregate_model_recall', 
            'aggregate_model_f1_score', 
            'aggregate_tp', 
            'aggregate_fp', 
            'aggregate_fn', 
            'semantic_similarity_average',
            'cosine_similarity_tfidf',
            'cosine_similarity_word2vec',
            'ragas_faithfulness',
            'ragas_answer_similarity'        
        ]
        model_level_df = model_level_df.reindex(columns=desired_order_model_level)
        return model_level_df

    def reorder_class_level_df(self, class_level_df):
        desired_order_class_level = [
            'class_name_model1',
            'class_name_model2',
            'class_attributes_model1',
            'class_attributes_model2',
            'class_attributes_diff_model1_minus_model2',
            'class_attributes_diff_model2_minus_model1',
            'attributes_tp',
            'attributes_fp',
            'attributes_fn',
            'class_references_model1',
            'class_references_model2',
            'class_references_diff_model1_minus_model2',
            'class_references_diff_model2_minus_model1',
            'references_tp',
            'references_fp',
            'references_fn',
            # 'class_regular_references_model1',
            # 'class_regular_references_model2',
            # 'class_regular_references_diff_model1_minus_model2',
            # 'class_regular_references_diff_model2_minus_model1',
            # 'regular_references_tp',
            # 'regular_references_fp',
            # 'regular_references_fn',
            # 'class_containments_model1',
            # 'class_containments_model2',
            # 'class_containments_diff_model1_minus_model2',
            # 'class_containments_diff_model2_minus_model1',
            # 'containments_tp',
            # 'containments_fp',
            # 'containments_fn',
            'class_superTypes_model1',
            'class_superTypes_model2',
            'superTypes_tp',
            'superTypes_fp',
            'superTypes_fn',
            'class_superTypes_diff_model1_minus_model2',
            'class_superTypes_diff_model2_minus_model1',
            'class_operations_model1',
            'class_operations_model2',
            'class_operations_diff_model1_minus_model2',
            'class_operations_diff_model2_minus_model1',
            'operations_tp',
            'operations_fp',
            'operations_fn',
            'aggregate_tp',
            'aggregate_fp',
            'aggregate_fn',
            'aggregate_class_precision',
            'aggregate_class_recall',
            'aggregate_class_f1_score',
            'semantic_similarity_average'            
        ]
        class_level_df = class_level_df.reindex(columns=desired_order_class_level)
        return class_level_df

    def rename_model_level_columns(self, model_df):
        names = {
            "model1_identifier": "model1_identifier",
            "model2_identifier": "model2_identifier",
            "total_classes_model1": "classes_model1_total",
            "total_classes_model2": "classes_model2_total",
            "total_classes_diff_model1_minus_model2": "classes_diff_model1_minus_model2_total",
            "total_classes_diff_model2_minus_model1": "classes_diff_model2_minus_model1_total",
            "classes_tp": "classes_tp",
            "classes_fp": "classes_fp",
            "classes_fn": "classes_fn",
            "total_attributes_model1": "attributes_model1_total",
            "total_attributes_model2": "attributes_model2_total",
            "attributes_tp": "attributes_tp",
            "attributes_fn": "attributes_fn",
            "attributes_fp": "attributes_fp",
            "total_attributes_diff_model1_minus_model2": "attributes_diff_model1_minus_model2_total",
            "total_attributes_diff_model2_minus_model1": "attributes_diff_model2_minus_model1_total",
            "total_operations_model1": "operations_model1_total",
            "total_operations_model2": "operations_model2_total",
            "total_operations_diff_model1_minus_model2": "operations_diff_model1_minus_model2_total",
            "total_operations_diff_model2_minus_model1": "operations_diff_model2_minus_model1_total",
            "operations_tp": "operations_tp",
            "operations_fn": "operations_fn",
            "operations_fp": "operations_fp",
            # "total_regular_references_model1": "regular_references_model1_total",
            # "total_regular_references_model2": "regular_references_model2_total",
            # "total_regular_references_diff_model2_minus_model1": "regular_references_diff_model2_minus_model1_total",
            # "total_regular_references_diff_model1_minus_model2": "regular_references_diff_model1_minus_model2_total",
            # "regular_references_tp": "regular_references_tp",
            # "regular_references_fp": "regular_references_fp",
            # "regular_references_fn": "regular_references_fn",
            # "total_containments_model1": "containments_model1_total",
            # "total_containments_model2": "containments_model2_total",
            # "total_containments_diff_model1_minus_model2": "containments_diff_model1_minus_model2_total",
            # "total_containments_diff_model2_minus_model1": "containments_diff_model2_minus_model1_total",
            # "containments_tp": "containments_tp",
            # "containments_fp": "containments_fp",
            # "containments_fn": "containments_fn",
            "total_references_model1": "references_model1_total",
            "total_references_model2": "references_model2_total",
            "total_references_diff_model1_minus_model2": "references_diff_model1_minus_model2_total",
            "total_references_diff_model2_minus_model1": "references_diff_model2_minus_model1_total",
            "references_tp": "references_tp",            
            "references_fp": "references_fp",
            "references_fn": "references_fn",
            "total_enumerations_model1": "enumerations_model1_total",
            "total_enumerations_model2": "enumerations_model2_total",
            "total_enumerations_diff_model1_minus_model2": "enumerations_diff_model1_minus_model2_total",
            "total_enumerations_diff_model2_minus_model1": "enumerations_diff_model2_minus_model1_total",
            "enumerations_tp": "enumerations_tp",
            "enumerations_fp": "enumerations_fp",
            "enumerations_fn": "enumerations_fn",
            "total_superTypes_model1": "superTypes_model1_total",
            "total_superTypes_model2": "superTypes_model2_total",
            "total_superTypes_diff_model1_minus_model2": "superTypes_diff_model1_minus_model2_total",
            "total_superTypes_diff_model2_minus_model1": "superTypes_diff_model2_minus_model1_total",
            "superTypes_tp": "superTypes_tp",
            "superTypes_fp": "superTypes_fp",
            "superTypes_fn": "superTypes_fn",
            "aggregate_model_precision": "aggregate_model_precision",
            "aggregate_model_recall": "aggregate_model_recall",
            "aggregate_model_f1_score": "aggregate_model_f1_score",
            "aggregate_tp": "aggregate_tp",
            "aggregate_fp": "aggregate_fp",
            "aggregate_fn": "aggregate_fn",
            "semantic_similarity_average": "semantic_similarity_average",
            "cosine_similarity_tfidf": "cosine_similarity_tfidf",
            "cosine_similarity_word2vec": "cosine_similarity_word2vec",
            "ragas_faithfulness": "ragas_faithfulness",
            "ragas_answer_similarity": "ragas_answer_similarity"        
        }
        model_df = model_df.rename(columns=names)
        return model_df

    def rename_class_level_columns(self, class_level_df):
        names = {
            "class_name_model1": "class_name_model1",
            "class_name_model2": "class_name_model2",
            "class_attributes_model1": "class_attributes_model1",
            "class_attributes_model2": "class_attributes_model2",
            "class_attributes_diff_model1_minus_model2": "class_attributes_diff_model1_minus_model2",
            "class_attributes_diff_model2_minus_model1": "class_attributes_diff_model2_minus_model1",
            "attributes_tp": "attributes_tp",
            "attributes_fp": "attributes_fp",
            "attributes_fn": "attributes_fn",
            "class_references_model1": "class_references_model1",
            "class_references_model2": "class_references_model2",
            "class_references_diff_model1_minus_model2": "class_references_diff_model1_minus_model2",
            "class_references_diff_model2_minus_model1": "class_references_diff_model2_minus_model1",
            "references_tp": "references_tp",
            "references_fp": "references_fp",
            "references_fn": "references_fn",
            # "class_regular_references_model1": "class_regular_references_model1",
            # "class_regular_references_model2": "class_regular_references_model2",
            # "class_regular_references_diff_model1_minus_model2": "class_regular_references_diff_model1_minus_model2",
            # "class_regular_references_diff_model2_minus_model1": "class_regular_references_diff_model2_minus_model1",
            # "regular_references_tp": "regular_references_tp",
            # "regular_references_fp": "regular_references_fp",
            # "regular_references_fn": "regular_references_fn",
            # "class_containments_model1": "class_containments_model1",
            # "class_containments_model2": "class_containments_model2",
            # "class_containments_diff_model1_minus_model2": "class_containments_diff_model1_minus_model2",
            # "class_containments_diff_model2_minus_model1": "class_containments_diff_model2_minus_model1",
            # "containments_tp": "containments_tp",
            # "containments_fp": "containments_fp",
            # "containments_fn": "containments_fn",
            "class_superTypes_model1": "class_superTypes_model1",
            "class_superTypes_model2": "class_superTypes_model2",
            "superTypes_tp": "superTypes_tp",
            "superTypes_fp": "superTypes_fp",
            "superTypes_fn": "superTypes_fn",
            "class_superTypes_diff_model1_minus_model2": "class_superTypes_diff_model1_minus_model2",
            "class_superTypes_diff_model2_minus_model1": "class_superTypes_diff_model2_minus_model1",
            "class_operations_model1": "class_operations_model1",
            "class_operations_model2": "class_operations_model2",
            "class_operations_diff_model1_minus_model2": "class_operations_diff_model1_minus_model2",
            "class_operations_diff_model2_minus_model1": "class_operations_diff_model2_minus_model1",
            "operations_tp": "operations_tp",
            "operations_fp": "operations_fp",
            "operations_fn": "operations_fn",
            "aggregate_tp": "aggregate_tp",
            "aggregate_fp": "aggregate_fp",
            "aggregate_fn": "aggregate_fn",
            "aggregate_class_precision": "aggregate_class_precision",
            "aggregate_class_recall": "aggregate_class_recall",
            "aggregate_class_f1_score": "aggregate_class_f1_score",
            "semantic_similarity_average": "semantic_similarity_average"
        }
        class_level_df = class_level_df.rename(columns=names)
        return class_level_df

    def create_csv(self, model_level_json, class_level_json, output_dir):
        list_of_class_level_csv_paths = []
        list_of_model_level_csv_paths = []
        consolidated_csv_paths = []
        for project in sorted(list(class_level_json.keys())):
            df = pd.DataFrame.from_dict(class_level_json[project], orient='index')
            df = self.reorder_class_level_df(df)
            df = self.rename_class_level_columns(df)
            class_csv_path = f'{output_dir}/class-analysis.csv'
            df.to_csv(class_csv_path)
            list_of_class_level_csv_paths.append(class_csv_path)

            model_level_json[project]["model2_identifier"] = model_level_json[project]["model2_identifier"][model_level_json[project]["model2_identifier"].rfind("/") + 1: len(model_level_json[project]["model2_identifier"])]
            model_level_json[project]["model1_identifier"] = f'{model_level_json[project]["model1_identifier"][model_level_json[project]["model1_identifier"].rfind("/") + 1: len(model_level_json[project]["model1_identifier"])]}'
            df_model = pd.DataFrame.from_dict({"values":model_level_json[project]}, orient='index')
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
    
    def run(self, ):
        model_level_json = {}
        class_level_json = {}

        projectName = "ecommerce-backend"
        config = "config.json"
        groundTruthModel = "ase2024-dataset/ecommerce-backend/ground-truth/ecommerce2.ecore"
        predictedModel_emf = "ase2024-dataset/ecommerce-backend/mdre-llm/haiku/fine/ecommerce-backend.emf"

        output_dir = f'{os.path.dirname(predictedModel_emf)}/stats'
        os.makedirs(output_dir, exist_ok=True)

        predictedModel = Adapter.get_ecore_model_from_emfatic(predictedModel_emf)

        response = Adapter.compare_ecore_models("bt_openlink.ecore", "btopenlinkjavacoremodel.ecore", projectName, config)
        model_level_json = response['result']["modelLevelJson"]
        class_level_json = response['result']["classLevelJson"]
        print(json.dumps(model_level_json, indent=4))

        consolidated_csv_paths = self.create_csv(model_level_json, class_level_json, output_dir)
        
        for consolidated_csv in consolidated_csv_paths:
            visualizations.box_and_whisker_for_model_level_metrics_from_consolidated(consolidated_csv, output_dir)
            visualizations.box_and_whisker_for_model_level_counts_from_consolidated(consolidated_csv, output_dir)                

if __name__ == '__main__':
    Main().run()
