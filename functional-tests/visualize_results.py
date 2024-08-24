import streamlit as st
import pandas as pd
import numpy as np
import json
from utils import generate_visualizations

df = pd.read_csv("evaluate_travis/results.csv")
st.write("Results")
st.dataframe(df)

msqe_syntactic = ((df['expected_f1_score'] - df['comparit_f1_score']) ** 2).mean()
msqe_semantic = ((df['expected_f1_score'] - df['SEMANTIC_SIMILARITY']) ** 2).mean()
se_syntactic = sum(abs(df['expected_f1_score'] - df['comparit_f1_score']))
se_semantic = sum(abs(df['expected_f1_score'] - df['SEMANTIC_SIMILARITY']))
st.write("Aggregate Results")
results_summary = pd.DataFrame(columns=["Sum of abs Semantic Error", "Sum of abs Syntactic Error", "Semantic Similarity MSQE", "Sntactic Similarity MSQE"], data = [[se_semantic, se_syntactic, msqe_semantic, msqe_syntactic]]).T
st.dataframe(results_summary)

values = df["predicted_model"].values
option = st.selectbox("Select Model Pair to view individual results", values)

model_level_json = {}
class_level_json = {}
with open(f'{option}/model_level_json.json', 'r') as fr:
	model_level_json = json.loads(fr.read())
with open(f'{option}/class_level_json.json', 'r') as fr:
	class_level_json = json.loads(fr.read())

col1, col2 = st.columns(2)
with col1:
	base_model = ""
	try:
		path = option[:len(option) - 1] + "1/mutant_1.ecore"
		with open(path) as fr:
			base_model = fr.read()
	except Exception as e:
		base_model = "Model not found"
	st.text_area("base_model", base_model, height = 500)

with col2:
    path = f'{option}/{option[option.rindex("/"):]}.ecore'
    with open(path) as fr:
        predicted_model = fr.read()
    st.text_area("predicted model", predicted_model, height = 500)

col1, col2 = st.columns(2)
with col1:
	base_model = ""
	try:
		path = option[:len(option) - 1] + "1/mutant_1.emf"
		with open(path) as fr:
			base_model = fr.read()
	except Exception as e:
		base_model = "Model not found"
	st.text_area("base_model", base_model, height = 500)

with col2:
    path = f'{option}/{option[option.rindex("/"):]}.emf'
    with open(path) as fr:
        predicted_model = fr.read()
    st.text_area("predicted model", predicted_model, height = 500)

generate_visualizations(model_level_json, class_level_json)