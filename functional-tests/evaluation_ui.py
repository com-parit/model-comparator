import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
from utils import generate_visualizations, summarize_results_of_bulk_comparison
import json

def interface_for_viewing_evaluation_results(csv_path):
    df = pd.read_csv(csv_path)
    st.write("Results")
    st.dataframe(df)

    msqe_syntactic = ((df['expected_f1_score'] - df['comparit_f1_score']) ** 2).mean()
    msqe_semantic = ((df['expected_f1_score'] - df['SEMANTIC_SIMILARITY']) ** 2).mean()
    se_syntactic = sum(abs(df['expected_f1_score'] - df['comparit_f1_score']))
    se_semantic = sum(abs(df['expected_f1_score'] - df['SEMANTIC_SIMILARITY']))
    st.write("Aggregate Results")
    results_summary = pd.DataFrame(columns=["Sum of abs Semantic Error", "Sum of abs Syntactic Error", "Semantic Similarity MSQE", "Sntactic Similarity MSQE"], data = [[se_semantic, se_syntactic, msqe_semantic, msqe_syntactic]]).T
    st.dataframe(results_summary)

    fig = px.box(df, y=["COMPARIT_ERROR", "SEMANTIC_SIMILARITY_ERROR"])
    st.plotly_chart(fig)

    fig = px.box(df, y=["ground_truth_model_size"])
    st.plotly_chart(fig)

    keys = []
    if "total_classes_model1" in df:
        keys.append("total_classes_model1")
    if "total_classes_model2" in df:
        keys.append("total_classes_model2")

    if "total_attributes_model1" in df:
        keys.append("total_attributes_model1")
    if "total_attributes_model2" in df:
        keys.append("total_attributes_model2")

    if "total_references_model1" in df:
        keys.append("total_references_model1")
    if "total_references_model2" in df:
        keys.append("total_references_model2")

    if "total_operations_model1" in df:
        keys.append("total_operations_model1")
    if "total_operations_model2" in df:
        keys.append("total_operations_model2")

    if "total_superTypes_model1" in df:
        keys.append("total_superTypes_model1")
    if "total_superTypes_model2" in df:
        keys.append("total_superTypes_model2")

    if "total_enumerations_model1" in df:
        keys.append("total_enumerations_model1")
    if "total_enumerations_model2" in df:
        keys.append("total_enumerations_model2")

    fig = px.box(df, y=keys)
    st.plotly_chart(fig)

    # values = df["predicted_model"].values
    # option = st.selectbox("Select Model Pair to view individual results", values)

    # model_level_json = {}
    # class_level_json = {}
    # with open(f'{option}/model_level_json.json', 'r') as fr:
    #     model_level_json = json.loads(fr.read())
    # with open(f'{option}/class_level_json.json', 'r') as fr:
    #     class_level_json = json.loads(fr.read())

    # col1, col2 = st.columns(2)
    # with col1:
    #     base_model = ""
    #     try:
    #         path = option[:option.rindex("/")] + "/base_model.ecore"
    #         with open(path) as fr:
    #             base_model = fr.read()
    #     except Exception as e:
    #         base_model = "Model not found"
    #     st.text_area("base_model_ecore", base_model, height = 500)

    # with col2:
    #     path = f'{option}/{option[option.rindex("/"):]}.ecore'
    #     with open(path) as fr:
    #         predicted_model = fr.read()
    #     st.text_area("predicted model ecore", predicted_model, height = 500)

    # col1, col2 = st.columns(2)
    # with col1:
    #     base_model = ""
    #     try:
    #         path = option[:option.rindex("/")] + "/base_model.emf"
    #         with open(path) as fr:
    #             base_model = fr.read()
    #     except Exception as e:
    #         base_model = "Model not found"
    #     st.text_area("base_model_emfatic", base_model, height = 500)

    # with col2:
    #     path = f'{option}/{option[option.rindex("/"):]}.emf'
    #     with open(path) as fr:
    #         predicted_model = fr.read()
    #     st.text_area("predicted model emfatic", predicted_model, height = 500)

    # generate_visualizations(model_level_json, class_level_json)
    
    # col1, col2 = st.columns(2)
    # with col1:
    #     st.text_area("base_model_ecore", model_level_json, height = 500)

    # with col2:
    #     path = f'{option}/expected_results.json'
    #     with open(path) as fr:
    #         expected_results = fr.read()
    #     st.text_area("expected results", expected_results, height = 500)

user_choice = st.selectbox(
    "What do you want to do?", 
    ["Choose", 
     "Hashing (.95) with aggregation", 
     "Hashing (.95) without aggregation",
     "Digest with aggregation", 
     "Digest without aggregation"
    ])

if user_choice == "Hashing (.95) with aggregation":
    interface_for_viewing_evaluation_results("evaluation_results_hashing_0.95_aggregate_true.csv")
if user_choice == "Hashing (.95) without aggregation":
    interface_for_viewing_evaluation_results("evaluation_results_hashing_0.95_aggregate_false.csv")
if user_choice == "Digest with aggregation":
    interface_for_viewing_evaluation_results("evaluation_results_digest_aggregate_true.csv")
if user_choice == "Digest without aggregation":
    interface_for_viewing_evaluation_results("evaluation_results_digest_aggregate_false.csv")
