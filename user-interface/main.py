import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import json
from adapter import Adapter

st.title('Comparit')

model_type = st.selectbox("Choose Model Type", ["None", "Ecore", "Emf"])

col1, col2 = st.columns(2)
if model_type == "Ecore":
    with col1:
        ground_truth_model = st.text_area(
            "Input Ecore Ground Truth Model",
        )
    with col2:
        predicted_model = st.text_area(
            "Input Ecore Predicted Model"
        )
elif model_type == "Emf":
    with col1:
        ground_truth_model = st.text_area(
            "Input EMFatic Truth Model",
        )
    with col2:
        predicted_model = st.text_area(
            "Input EMFatic Predicted Model"
        )
    
st.write("Configuration Panel")
with st.container(height=400, border=True):    
    config_col1, config_col2, config_col3, config_col4, config_col5 = st.columns(5)

    with config_col1:
        USE_HASHING = st.selectbox("Use Hashing", ["True", "False"]) == "True"
        HASHING_THRESHOLD = st.text_input("Hashing Threshold", value="0.95")

    with config_col2:
        INCLUDE_CLASS_ATTRIBUTES = st.selectbox("Include Class Attributes", ["True", "False"]) == "True"
        INCLUDE_CLASS_REFERENCES = st.selectbox("Include Class References", ["True", "False"]) == "True"
        INCLUDE_CLASS_OPERATIONS = st.selectbox("Include Class Operations", ["True", "False"]) == "True"
        INCLUDE_CLASS_SUPERTYPES = st.selectbox("Include Class Supertypes", ["True", "False"]) == "True"
        INCLUDE_ENUMS = st.selectbox("Include Enums", ["True", "False"]) == "True"

    with config_col3:
        INCLUDE_ATTRIBUTE_NAME = st.selectbox("Include Attribute Name", ["True", "False"]) == "True"
        INCLUDE_ATTRIBUTE_CONTAINING_CLASS = st.selectbox("Include Attribute Containing Class", ["True", "False"]) == "True"
        INCLUDE_ATTRIBUTE_TYPE = st.selectbox("Include Attribute Type", ["True", "False"]) == "True"
        INCLUDE_ATTRIBUTE_LOWER_BOUND = st.selectbox("Include Attribute Lower Bound", ["True", "False"]) == "True"
        INCLUDE_ATTRIBUTE_UPPER_BOUND = st.selectbox("Include Attribute Upper Bound", ["True", "False"]) == "True"
        INCLUDE_ATTRIBUTE_IS_ORDERED = st.selectbox("Include Attribute Is Ordered", ["True", "False"]) == "True"
        INCLUDE_ATTRIBUTE_IS_UNIQUE = st.selectbox("Include Attribute Is Unique", ["True", "False"]) == "True"

    with config_col4:
        INCLUDE_REFERENCES_NAME = st.selectbox("Include References Name", ["True", "False"]) == "True"
        INCLUDE_REFERENCES_CONTAINING_CLASS = st.selectbox("Include References Containing Class", ["True", "False"]) == "True"
        INCLUDE_REFERENCES_IS_CONTAINMENT = st.selectbox("Include References Is Containment", ["True", "False"]) == "True"
        INCLUDE_REFERENCES_LOWER_BOUND = st.selectbox("Include References Lower Bound", ["True", "False"]) == "True"
        INCLUDE_REFERENCES_UPPER_BOUND = st.selectbox("Include References Upper Bound", ["True", "False"]) == "True"
        INCLUDE_REFERENCES_IS_ORDERED = st.selectbox("Include References Is Ordered", ["True", "False"]) == "True"
        INCLUDE_REFERENCES_IS_UNIQUE = st.selectbox("Include References Is Unique", ["True", "False"]) == "True"

    with config_col5:
        INCLUDE_OPERATION_NAME = st.selectbox("Include Operation Name", ["True", "False"]) == "True"
        INCLUDE_OPERATION_CONTAINING_CLASS = st.selectbox("Include Operation Containing Class", ["True", "False"]) == "True"
        INCLUDE_OPERATION_PARAMETERS = st.selectbox("Include Operation Parameters", ["True", "False"]) == "True"
        INCLUDE_PARAMETER_NAME = st.selectbox("Include Parameter Name", ["True", "False"]) == "True"
        INCLUDE_PARAMETER_TYPE = st.selectbox("Include Parameter Type", ["True", "False"]) == "True"
        INCLUDE_PARAMETER_OPERATION_NAME = st.selectbox("Include Parameter Operation Name", ["True", "False"]) == "True"
    
if st.button("Compare", type="primary"):
    config = {
        "USE_HASHING": USE_HASHING,
        "HASHING_THRESHOLD": float(HASHING_THRESHOLD),
        "INCLUDE_CLASS_ATTRIBUTES": INCLUDE_CLASS_ATTRIBUTES,
        "INCLUDE_CLASS_REFERENCES": INCLUDE_CLASS_REFERENCES,
        "INCLUDE_CLASS_OPERATIONS": INCLUDE_CLASS_OPERATIONS,
        "INCLUDE_CLASS_SUPERTYPES": INCLUDE_CLASS_SUPERTYPES,
        "INCLUDE_ENUMS": INCLUDE_ENUMS,
        "INCLUDE_ATTRIBUTE_NAME": INCLUDE_ATTRIBUTE_NAME,
        "INCLUDE_ATTRIBUTE_CONTAINING_CLASS": INCLUDE_ATTRIBUTE_CONTAINING_CLASS,
        "INCLUDE_ATTRIBUTE_TYPE": INCLUDE_ATTRIBUTE_TYPE,
        "INCLUDE_ATTRIBUTE_LOWER_BOUND": INCLUDE_ATTRIBUTE_LOWER_BOUND,
        "INCLUDE_ATTRIBUTE_UPPER_BOUND": INCLUDE_ATTRIBUTE_UPPER_BOUND,
        "INCLUDE_ATTRIBUTE_IS_ORDERED": INCLUDE_ATTRIBUTE_IS_ORDERED,
        "INCLUDE_ATTRIBUTE_IS_UNIQUE": INCLUDE_ATTRIBUTE_IS_UNIQUE,
        "INCLUDE_REFERENCES_NAME": INCLUDE_REFERENCES_NAME,
        "INCLUDE_REFERENCES_CONTAINING_CLASS": INCLUDE_REFERENCES_CONTAINING_CLASS,
        "INCLUDE_REFERENCES_IS_CONTAINMENT": INCLUDE_REFERENCES_IS_CONTAINMENT,
        "INCLUDE_REFERENCES_LOWER_BOUND": INCLUDE_REFERENCES_LOWER_BOUND,
        "INCLUDE_REFERENCES_UPPER_BOUND": INCLUDE_REFERENCES_UPPER_BOUND,
        "INCLUDE_REFERENCES_IS_ORDERED": INCLUDE_REFERENCES_IS_ORDERED,
        "INCLUDE_REFERENCES_IS_UNIQUE": INCLUDE_REFERENCES_IS_UNIQUE,
        "INCLUDE_OPERATION_NAME": INCLUDE_OPERATION_NAME,
        "INCLUDE_OPERATION_CONTAINING_CLASS": INCLUDE_OPERATION_CONTAINING_CLASS,
        "INCLUDE_OPERATION_PARAMETERS": INCLUDE_OPERATION_PARAMETERS,
        "INCLUDE_PARAMETER_NAME": INCLUDE_PARAMETER_NAME,
        "INCLUDE_PARAMETER_TYPE": INCLUDE_PARAMETER_TYPE,
        "INCLUDE_PARAMETER_OPERATION_NAME": INCLUDE_PARAMETER_OPERATION_NAME
    }

    # Save to JSON file
    with open("config_user.json", "w") as outfile:
        json.dump(config, outfile, indent=4)
    st.write("Comparing...")
    config_file_path = "config_user.json"
    ground_truth_path = "ground_truth_model.ecore" if model_type == "Ecore" else "ground_truth_model.emf"
    predicted_path = "predicted_model.ecore" if model_type == "Ecore" else "predicted_model.emf"
    with open(config_file_path, 'w') as fr:
        fr.write(str(config))  
    with open(ground_truth_path, 'w') as fr:
        fr.write(str(ground_truth_model))  
    with open(predicted_path, 'w') as fr:
        fr.write(str(predicted_model))  
        
    if model_type == "Ecore":
        model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(ground_truth_path, predicted_path, config_file_path)
    elif model_type == "Emf":
        model_level_json, class_level_json = Adapter.compare_emfatic_models_syntactically_and_semantically(ground_truth_path, predicted_path, config_file_path)
    
    with open("model_level_json.json", 'w') as fr:
        fr.write(json.dumps(model_level_json, indent=4)) 
    with open("class_level_json.json", 'w') as fr:
        fr.write(json.dumps(class_level_json, indent=4)) 

    try:
        bar_df = pd.DataFrame({'x': [
        "precision", 
        "recall", 
        "f1_score", 
        "cosine_similarity_SMOTE", 
        # "cosine_similarity_word2vec",
        #  "ragas_answer_similarity"
        ], 'y': [
            model_level_json["aggregate_model_precision"], 
            model_level_json["aggregate_model_recall"], 
            model_level_json["aggregate_model_f1_score"],
            model_level_json["cosine_similarity_SMOTE"],
            # model_level_json["cosine_similarity_word2vec"],
            # model_level_json["ragas_answer_similarity"] if model_level_json["ragas_answer_similarity"] > 0 else 0 
        ]
        })
        fig = px.bar(bar_df, x='x', y='y', title='Model Level Metrics')
        st.plotly_chart(fig)
    except Exception as e:
        print(e)

    colors = ['green', 'grey', 'red']

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["classes_tp"],
            model_level_json["classes_fp"],
            model_level_json["classes_fn"]
        ]})
        pie = px.pie(pie_df, values='values', names='element', title='Classes')
        pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
        st.plotly_chart(pie)
    except Exception as e:
        print(e)

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["attributes_tp"],
            model_level_json["attributes_fp"],
            model_level_json["attributes_fn"]
        ]})
        pie = px.pie(pie_df, values='values', names='element', title='Attributes')
        pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
        st.plotly_chart(pie)
    except Exception as e:
        print(e)

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["operations_tp"],
            model_level_json["operations_fp"],
            model_level_json["operations_fn"]
        ]})
        pie = px.pie(pie_df, values='values', names='element', title='Operations')
        pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
        st.plotly_chart(pie)
    except Exception as e:
        print(e)


    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["references_tp"],
            model_level_json["references_fp"],
            model_level_json["references_fn"]
        ]})
        pie = px.pie(pie_df, values='values', names='element', title='References')
        pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
        st.plotly_chart(pie)
    except Exception as e:
        print(e)

    try:
        pie_df = pd.DataFrame({'element': ["Correctly Identified", "Only in Model 2", "Only in Model 1"], 'values': [
            model_level_json["superTypes_tp"],
            model_level_json["superTypes_fp"],
            model_level_json["superTypes_fn"]
        ]})
        pie = px.pie(pie_df, values='values', names='element', title=f'Supertypes')
        pie.update_traces(textposition='inside', textinfo='value', marker=dict(colors=colors))
        st.plotly_chart(pie)
    except Exception as e:
        print(e)

    with open("class_level_json.json", "r") as cj:
        class_level_json = json.loads(cj.read())
        columns = [
            "name1", 
            "name2", 
            "attributes_tp",
            "attributes_fp",
            "attributes_fn",
            "references_tp",
            "references_fp",
            "references_fn",
            "superTypes_tp",
            "superTypes_fp",
            "superTypes_fn",
            "operations_tp",
            "operations_fp",
            "operations_fn",
        ]
        values = []
        for i in class_level_json:
            names1 = []
            names2 = []
            attributes_tp = []
            attributes_fp = []
            attributes_fn = []
            references_tp = []
            references_fp = []
            references_fn = []
            superTypes_tp = []
            superTypes_fp = []
            superTypes_fn = []
            operations_tp = []
            operations_fp = []
            operations_fn = []
            names1.append(class_level_json[i]["class_name_model1"])
            names2.append(class_level_json[i]["class_name_model2"])
            attributes_tp.append(class_level_json[i]["attributes_tp"])
            attributes_fp.append(class_level_json[i]["attributes_fp"])
            attributes_fn.append(class_level_json[i]["attributes_fn"])
            references_tp.append(class_level_json[i]["references_tp"])
            references_fp.append(class_level_json[i]["references_fp"])
            references_fn.append(class_level_json[i]["references_fn"])
            operations_tp.append(class_level_json[i]["operations_tp"])
            operations_fp.append(class_level_json[i]["operations_fp"])
            operations_fn.append(class_level_json[i]["operations_fn"])
            superTypes_tp.append(class_level_json[i]["superTypes_tp"])
            superTypes_fp.append(class_level_json[i]["superTypes_fp"])
            superTypes_fn.append(class_level_json[i]["superTypes_fn"])
            values.append(names1)
            values.append(names2)
            values.append(attributes_tp)
            values.append(attributes_fp)
            values.append(attributes_fn)
            values.append(references_tp)
            values.append(references_fp)
            values.append(references_fn)
            values.append(superTypes_tp)
            values.append(superTypes_fp)
            values.append(superTypes_fn)
            values.append(operations_tp)
            values.append(operations_fp)
            values.append(operations_fn)
        
        fig = go.Figure(data=[go.Table(
            header=dict(values=columns,
                line_color='darkslategray',
                fill_color='maroon',
                align='left'
            ),
            cells=dict(values=values,
                    line_color='darkslategray',
                    fill_color='black',
                    align='left'))])
        fig.update_layout(width=600, height=1000)
        st.plotly_chart(fig)
