import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
# Apply the default theme
import config as CONFIG
import json
from matplotlib.backends.backend_pdf import PdfPages
import os

sns.set_theme()

class Visualizations:    
	def draw_box_and_whisker_for_groovy_statistics(df, selected_columns, output_path):
		plot_values = {
			"modelset": {
				
			}
		}
		for i in selected_columns:
			plot_values["modelset"][i] = [i for i in df[i].values.tolist() if i < 100]

		with PdfPages(f'{output_path}/box_and_whisker_statistics_groovy_categories.pdf') as pdf:
			# transform json into longdata format
			data = []
			
			for model, metrics in plot_values.items():
				for metric, values in metrics.items():
					for value in values:
						metric = metric.replace("_", "\n")
						data.append([model, metric, value])
			
			df = pd.DataFrame(data, columns=['model', 'metric', 'value'])

			plt.figure(figsize=(8, 8))
			sns.boxplot(df, x="metric", y="value", hue="model", whis=(0,10), legend=False, gap=0.01, width=0.4)
			plt.title("Element Counts for Modelset", fontsize=19)
			plt.xticks(rotation=90, fontsize=19)
			plt.yticks(fontsize=19)
			plt.ylabel("", fontsize=19)
			plt.tight_layout()
			plt.xlabel("", fontsize=19)
			pdf.savefig()
			plt.savefig(f'{output_path}/box_and_whisker_statistics_groovy_categories.png')
			plt.close()

	def box_and_whisker_for_model_level_metrics_from_consolidated(consolidated_models_csv_path, output_path_folder):
		# Dictionary to store metrics values for each metric
		all_metrics_values = {
		"mdre-llm":{
			"precision": [],
			"recall": [],
			"f1_score": [],
			"cosine_similarity_tfidf": [],
			"cosine_similarity_word2vec": [],
			"ragas_faithfulness": [],
			"ragas_answer_similarity": []
		},
		"j2ecore-vjava": {
			"precision": [],
			"recall": [],
			"f1_score": [],
			"cosine_similarity_tfidf": [],
			"cosine_similarity_word2vec": [],
			"ragas_faithfulness": [],
			"ragas_answer_similarity": []
		}
		}

		consolidated_df = pd.read_csv(consolidated_models_csv_path)
		all_metrics_values["mdre-llm"]["precision"] = [i for i in consolidated_df["aggregate_model_precision"]]
		all_metrics_values["mdre-llm"]["recall"] = [i for i in consolidated_df["aggregate_model_recall"]]
		all_metrics_values["mdre-llm"]["f1_score"] = [i for i in consolidated_df["aggregate_model_f1_score"]]
		all_metrics_values["mdre-llm"]["cosine_similarity_tfidf"] = [i for i in consolidated_df["cosine_similarity_tfidf"]]
		# all_metrics_values["mdre-llm"]["cosine_similarity_tfidf"] = [i for i in consolidated_df["aggregate_model_precision"]]
		# all_metrics_values["mdre-llm"]["cosine_similarity_tfidf"] = [i for i in consolidated_df["aggregate_model_precision"]]

		# REMOVING MODELS THAT WE DO NOT WANT TO VISUALIZE
		del all_metrics_values["j2ecore-vjava"]
		# del all_metrics_values["mdre-llm"]["cosine_similarity_word2vec"]
		del all_metrics_values["mdre-llm"]["ragas_faithfulness"]
		del all_metrics_values["mdre-llm"]["ragas_answer_similarity"]

		# Consolidate box plots for all metrics into one figure
		with PdfPages(f'{output_path_folder}/box_and_whisker_for_model_level_metrics.pdf') as pdf:
			# transform json into longdata format
			data = []
			
			for model, metrics in all_metrics_values.items():
				for metric, values in metrics.items():
					for value in values:
						if metric == "cosine_similarity_tfidf":
							metric = "cosine\nsimilarity\ntfidf"
						if metric == "f1_score":
							metric = "f1\nscore"
						data.append([model, metric, value])
			
			df = pd.DataFrame(data, columns=['model', 'metric', 'value'])

			plt.figure(figsize=(8, 8))
			sns.boxplot(df, x="metric", y="value", hue="model", legend=False, width=0.4)
			plt.title("Box and Whisker Plot for Metrics", fontsize=19)
			plt.xticks(rotation=90, fontsize=19)
			plt.yticks(fontsize=19)
			plt.ylabel("")
			plt.xlabel("")
			plt.tight_layout()
			pdf.savefig()
			# Save as PNG
			plt.savefig(f'{output_path_folder}/box_and_whisker_for_model_level_metrics.png')
			plt.close()

	def box_and_whisker_for_model_level_counts_from_consolidated(consolidated_models_csv_path, output_path_folder):
		# Dictionary to store metrics values for each metric
		all_metrics_values = {
		"ground-truth":{
			"classes": [],
			"attributes": [],
			"operations": [],
			"enumerations": [],
			"superTypes": [],
			# "containments": [],
			"references": [],
			# "regular_references": []
		},
		"mdre-llm":{
			"classes": [],
			"attributes": [],
			"operations": [],
			"enumerations": [],
			"superTypes": [],
			# "containments": [],
			"references": [],
			# "regular_references": []
		}
		}
  
		consolidated_df = pd.read_csv(consolidated_models_csv_path)
		all_metrics_values["ground-truth"]["classes"] = [i for i in consolidated_df["classes_model1_total"]]
		all_metrics_values["ground-truth"]["attributes"] = [i for i in consolidated_df["attributes_model1_total"]]
		all_metrics_values["ground-truth"]["operations"] = [i for i in consolidated_df["operations_model1_total"]]
		all_metrics_values["ground-truth"]["enumerations"] = [i for i in consolidated_df["enumerations_model1_total"]]
		all_metrics_values["ground-truth"]["superTypes"] = [i for i in consolidated_df["superTypes_model1_total"]]
		all_metrics_values["ground-truth"]["references"] = [i for i in consolidated_df["references_model1_total"]]
		all_metrics_values["mdre-llm"]["classes"] = [i for i in consolidated_df["classes_model2_total"]]
		all_metrics_values["mdre-llm"]["attributes"] = [i for i in consolidated_df["attributes_model2_total"]]
		all_metrics_values["mdre-llm"]["operations"] = [i for i in consolidated_df["operations_model2_total"]]
		all_metrics_values["mdre-llm"]["enumerations"] = [i for i in consolidated_df["enumerations_model2_total"]]
		all_metrics_values["mdre-llm"]["superTypes"] = [i for i in consolidated_df["superTypes_model2_total"]]
		all_metrics_values["mdre-llm"]["references"] = [i for i in consolidated_df["references_model2_total"]]

		# Consolidate box plots for all metrics into one figure
		with PdfPages(f'{output_path_folder}/box_and_whisker_for_model_level_total_counts.pdf') as pdf:
			# transform json into longdata format
			data = []
			
			for model, metrics in all_metrics_values.items():
				for metric, values in metrics.items():
					for value in values:
						data.append([model, metric, value])
			
			df = pd.DataFrame(data, columns=['model', 'metric', 'value'])

			plt.figure(figsize=(12, 8))
			sns.boxplot(df, x="metric", y="value", hue="model")
			plt.title("Box and Whisker Plot for Total Counts")
			plt.xticks(rotation=90)
			plt.ylabel("")
			plt.tight_layout()
			pdf.savefig()
			plt.savefig(f'{output_path_folder}/box_and_whisker_for_model_level_counts.png')
			plt.close()

# df = pd.read_csv("/media/jawad/secondaryStorage/projects/mdre/caseStudies/statistics_groovy_categories.csv")
# df = df.rename(columns={
# 	'c_count': 'classes_count',
# 	'att_count': 'attributes_count',
# 	'ref_count': 'references_count',
# 	'cont_count': 'containments_count',
# 	'super_count': 'superTypes_count'
# })
# Visualizations.draw_box_and_whisker(df, ['classes_count', 'attributes_count','references_count', 'containments_count', 'superTypes_count'], ".")
