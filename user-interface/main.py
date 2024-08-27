import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import json
from adapter import Adapter
from constants import CONSTANTS
from utils import generate_visualizations
import io
import zipfile
import os

# Function to save files to the desired location
def save_file(file, save_path):
    with open(save_path, 'wb') as f:
        f.write(file.read())
st.title('Comparit')

def interface_for_comparing_ecore():
    option = st.selectbox(
        "Sample Model Pairs",
        ("None", "carAndBottle", "jmotiff"),
    )
    sample_ground_truth = "Input Ecore Ground Truth Model"
    sample_predicted_model = "Input Ecore Predicted Truth Model"
    if option != "None":
        try:
            sample_ground_truth = CONSTANTS.SAMPLE_ECORE_MODEL_PAIRS.value[option][0]
            sample_predicted_model = CONSTANTS.SAMPLE_ECORE_MODEL_PAIRS.value[option][1]
        except Exception as e:
            sample_ground_truth = "The selected model could not be loaded please try some other model"
            sample_predicted_model = "The selected model could not be loaded please try some other model"
        
    col1, col2 = st.columns(2)
    with col1:
        ground_truth_model = st.text_area(
            "Input Ecore Ground Truth Model", value=sample_ground_truth, height = 500
        )
    with col2:
        predicted_model = st.text_area(
            "Input Ecore Predicted Truth Model", value=sample_predicted_model, height = 500
        )
    st.write("Configuration Panel")
    with st.container(height=400, border=True):    
        config_col1, config_col2, config_col3, config_col4, config_col5 = st.columns(5)

        with config_col1:
            USE_HASHING = st.selectbox("Use Hashing", ["True", "False"]) == "True"
            HASHING_THRESHOLD = st.text_input("Hashing Threshold", value="0.95")
            MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON = st.selectbox("Aggregate Class Level Metrics", ["True", "False"]) == "True"

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
            "MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON": MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON,
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
        config_file_path = "config_user.json"
        ground_truth_path = "ground_truth_model.ecore"
        predicted_path = "predicted_model.ecore"
        with open(config_file_path, 'w') as fr:
            fr.write(str(config))  
        with open(ground_truth_path, 'w') as fr:
            fr.write(str(ground_truth_model))  
        with open(predicted_path, 'w') as fr:
            fr.write(str(predicted_model))  
            
        model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(ground_truth_path, predicted_path, config_file_path)
                
        st.write(f'Took {model_level_json["time in milliseconds for syntantic comparison"]} for sytnactic comparison')
        with open("model_level_json.json", 'w') as fr:
            fr.write(json.dumps(model_level_json, indent=4)) 
        with open("class_level_json.json", 'w') as fr:
            fr.write(json.dumps(class_level_json, indent=4)) 

        generate_visualizations(model_level_json, class_level_json)

def interface_for_comparing_emfatic():
    option = st.selectbox(
        "Sample Model Pairs",
        ("None", "carAndBottle", "jmotiff"),
    )
    sample_ground_truth = "Input EMFATIC Ground Truth Model"
    sample_predicted_model = "Input EMFATIC Predicted Truth Model"
    if option != "None":
        try:
            sample_ground_truth = CONSTANTS.SAMPLE_EMFATIC_MODEL_PAIRS.value[option][0]
            sample_predicted_model = CONSTANTS.SAMPLE_EMFATIC_MODEL_PAIRS.value[option][1]
        except Exception as e:
            sample_ground_truth = "The selected model could not be loaded please try some other model"
            sample_predicted_model = "The selected model could not be loaded please try some other model"    
    
    col1, col2 = st.columns(2)
    with col1:
        ground_truth_model = st.text_area(
            "Input EMFATIC Ground Truth Model", value=sample_ground_truth, height = 500
        )
    with col2:
        predicted_model = st.text_area(
            "Input EMFATIC Predicted Truth Model", value=sample_predicted_model, height = 500
        )    
    st.write("Configuration Panel")
    with st.container(height=400, border=True):    
        config_col1, config_col2, config_col3, config_col4, config_col5 = st.columns(5)

        with config_col1:
            USE_HASHING = st.selectbox("Use Hashing", ["True", "False"]) == "True"
            HASHING_THRESHOLD = st.text_input("Hashing Threshold", value="0.95")
            MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON = st.selectbox("Aggregate Class Level Metrics", ["True", "False"]) == "True"

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
            "MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON": MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON,
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
        config_file_path = "config_user.json"
        ground_truth_path = "ground_truth_model.emf"
        predicted_path = "predicted_model.emf"
        with open(config_file_path, 'w') as fr:
            fr.write(str(config))  
        with open(ground_truth_path, 'w') as fr:
            fr.write(str(ground_truth_model))  
        with open(predicted_path, 'w') as fr:
            fr.write(str(predicted_model))  
            
        model_level_json, class_level_json = Adapter.compare_emfatic_models_syntactically_and_semantically(ground_truth_path, predicted_path, config_file_path)
                
        st.write(f'Took {model_level_json["time in milliseconds for syntantic comparison"]} for sytnactic comparison')
        with open("model_level_json.json", 'w') as fr:
            fr.write(json.dumps(model_level_json, indent=4)) 
        with open("class_level_json.json", 'w') as fr:
            fr.write(json.dumps(class_level_json, indent=4)) 

        generate_visualizations(model_level_json, class_level_json)

def interface_for_comparing_uml():
    option = st.selectbox(
        "Sample Model Pairs",
        ("None", "sample"),
    )
    sample_ground_truth = "Input EMFATIC Ground Truth Model"
    sample_predicted_model = "Input EMFATIC Predicted Truth Model"
    if option != "None":
        try:
            sample_ground_truth = CONSTANTS.SAMPLE_UML2_MODEL_PAIRS.value[option][0]
            sample_predicted_model = CONSTANTS.SAMPLE_UML2_MODEL_PAIRS.value[option][1]
        except Exception as e:
            sample_ground_truth = "The selected model could not be loaded please try some other model"
            sample_predicted_model = "The selected model could not be loaded please try some other model"    
    
    col1, col2 = st.columns(2)
    with col1:
        ground_truth_model = st.text_area(
            "Input EMFATIC Ground Truth Model", value=sample_ground_truth, height = 500
        )
    with col2:
        predicted_model = st.text_area(
            "Input EMFATIC Predicted Truth Model", value=sample_predicted_model, height = 500
        )    
    st.write("Configuration Panel")
    with st.container(height=400, border=True):    
        config_col1, config_col2, config_col3, config_col4, config_col5 = st.columns(5)

        with config_col1:
            USE_HASHING = st.selectbox("Use Hashing", ["True", "False"]) == "True"
            HASHING_THRESHOLD = st.text_input("Hashing Threshold", value="0.95")
            MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON = st.selectbox("Aggregate Class Level Metrics", ["True", "False"]) == "True"

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
            "MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON": MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON,
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
        config_file_path = "config_user.json"
        ground_truth_path = "ground_truth_model.uml"
        predicted_path = "predicted_model.uml"
        with open(config_file_path, 'w') as fr:
            fr.write(str(config))  
        with open(ground_truth_path, 'w') as fr:
            fr.write(str(ground_truth_model))  
        with open(predicted_path, 'w') as fr:
            fr.write(str(predicted_model))  
            
        model_level_json, class_level_json = Adapter.compare_uml2_models_syntactically_and_semantically(ground_truth_path, predicted_path, config_file_path)
                
        st.write(f'Took {model_level_json["time in milliseconds for syntantic comparison"]} for sytnactic comparison')
        with open("model_level_json.json", 'w') as fr:
            fr.write(json.dumps(model_level_json, indent=4)) 
        with open("class_level_json.json", 'w') as fr:
            fr.write(json.dumps(class_level_json, indent=4)) 

        generate_visualizations(model_level_json, class_level_json)
                
def interface_for_bulk_ecore():
    uploaded_files = st.file_uploader(
        "Upload a zip file containing ecore model pairs", accept_multiple_files=True
    )
    pairs_count = 0
    pairs = []
    for uploaded_zip_folder in uploaded_files:
        if uploaded_zip_folder is not None:
            extract_dir = "extracted_files"

            if not os.path.exists(extract_dir):
                os.makedirs(extract_dir)

            with zipfile.ZipFile(io.BytesIO(uploaded_zip_folder.read()), 'r') as zip_ref:
                zip_ref.extractall(extract_dir)

            st.success('Zip file has been extracted.')
            for meta_sub_folder in os.listdir(extract_dir):
                meta_sub_folder_path = os.path.join(extract_dir, meta_sub_folder)
                for subfolder_name in os.listdir(meta_sub_folder_path):
                    subfolder_path = os.path.join(meta_sub_folder_path, subfolder_name)
                    
                    if os.path.isdir(subfolder_path):
                        st.write(f'Processing subfolder: {subfolder_name}')
                        pairs_count += 1
                        base_folder_path = f'{subfolder_path}/base'
                        predicted_folder_path = f'{subfolder_path}/predicted'
                        pair_obj = {"base": [], "predicted": []}
                        for file_name in os.listdir(base_folder_path):
                            base_model_path = os.path.join(base_folder_path, file_name)
                            pair_obj["base"].append(base_model_path)
                        for file_name in os.listdir(predicted_folder_path):
                            predicted_model_path = os.path.join(predicted_folder_path, file_name)
                            pair_obj["predicted"].append(predicted_model_path)
                        pairs.append(pair_obj)
            st.success('All subfolders have been processed.')

    st.write("Configuration Panel")
    with st.container(height=400, border=True):    
        config_col1, config_col2, config_col3, config_col4, config_col5 = st.columns(5)

        with config_col1:
            USE_HASHING = st.selectbox("Use Hashing", ["True", "False"]) == "True"
            HASHING_THRESHOLD = st.text_input("Hashing Threshold", value="0.95")
            MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON = st.selectbox("Aggregate Class Level Metrics", ["True", "False"]) == "True"

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
            "MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON": MODEL_LEVEL_COMPARISON_DERIVED_FROM_CLASS_LEVEL_COMPARISON,
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
        config_file_path = "config_user.json"
        for pair in pairs:
            for base_model in pair["base"]:
                for predicted_model in pair["predicted"]:
                    st.write(f'processing pair: {base_model} and {predicted_model}')
        
model_type = st.selectbox("Choose Comparison Type", ["None", "Ecore", "Emfatic", "UML2", "Bulk Ecore"])

if model_type == "Ecore":
    interface_for_comparing_ecore()
elif model_type == "Emfatic":
    interface_for_comparing_emfatic()
elif model_type == "UML2":
    interface_for_comparing_uml()
elif model_type == "Bulk Ecore":
    interface_for_bulk_ecore()    