import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import json
from adapter import Adapter
from constants import CONSTANTS
from utils import generate_visualizations, summarize_results_of_bulk_comparison
import io
import zipfile
import os
import plotly.express as px
import plotly.graph_objects as go
import json
import plotly.graph_objects as go
from datetime import datetime, timezone
import shutil

if 'bulk_extraction_directory' not in st.session_state:
    st.session_state['bulk_extraction_directory'] = ""

if 'model_level_json_map_bulk' not in st.session_state:
    st.session_state['model_level_json_map_bulk'] = {}

if 'class_level_json_map_bulk' not in st.session_state:
    st.session_state['class_level_json_map_bulk'] = {}

if 'model_pairs' not in st.session_state:
    st.session_state['model_pairs'] = []

if 'bulk_models_loaded' not in st.session_state:
    st.session_state['bulk_models_loaded'] = False

if 'bulk_models_df' not in st.session_state:
    st.session_state['bulk_models_df'] = None

def save_file(file, save_path):
    with open(save_path, 'wb') as f:
        f.write(file.read())
st.title('Comparit')

def interface_for_comparing_ecore():
    option = st.selectbox(
        "Sample Model Pairs",
        ("Choose", "sample"),
    )
    sample_ground_truth = "Input Ecore Ground Truth Model"
    sample_predicted_model = "Input Ecore Predicted Truth Model"
    if option != "Choose":
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
    st.subheader("Configuration Panel", divider=True)
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
        config_file_path = f'config_user_{datetime.now(timezone.utc).timestamp()}.json'
        with open(config_file_path, "w") as outfile:
            json.dump(config, outfile, indent=4)
        ground_truth_path = f'ground_truth_model{datetime.now(timezone.utc).timestamp()}.ecore'
        predicted_path = f'predicted_model{datetime.now(timezone.utc).timestamp()}.ecore'
        with open(config_file_path, 'w') as fr:
            fr.write(str(config))  
        with open(ground_truth_path, 'w') as fr:
            fr.write(str(ground_truth_model))  
        with open(predicted_path, 'w') as fr:
            fr.write(str(predicted_model))  
            
        model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(ground_truth_path, predicted_path, config_file_path)
                
        st.write(f'Took {model_level_json["time in milliseconds for syntantic comparison"]} for sytnactic comparison')
        generate_visualizations(model_level_json, class_level_json)


def interface_for_comparing_emfatic():
    option = st.selectbox(
        "Sample Model Pairs",
        ("Choose", "sample"),
    )
    sample_ground_truth = "Input EMFATIC Ground Truth Model"
    sample_predicted_model = "Input EMFATIC Predicted Truth Model"
    if option != "Choose":
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
    st.subheader("Configuration Panel", divider=True)
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
        config_file_path = f'config_user_{datetime.now(timezone.utc).timestamp()}.json'
        with open(config_file_path, "w") as outfile:
            json.dump(config, outfile, indent=4)
        ground_truth_path = f'ground_truth_model{datetime.now(timezone.utc).timestamp()}.emf'
        predicted_path = f'predicted_model{datetime.now(timezone.utc).timestamp()}.emf'
        with open(config_file_path, 'w') as fr:
            fr.write(str(config))  
        with open(ground_truth_path, 'w') as fr:
            fr.write(str(ground_truth_model))  
        with open(predicted_path, 'w') as fr:
            fr.write(str(predicted_model))  
            
        model_level_json, class_level_json = Adapter.compare_emfatic_models_syntactically_and_semantically(ground_truth_path, predicted_path, config_file_path)
                
        st.write(f'Took {model_level_json["time in milliseconds for syntantic comparison"]} for sytnactic comparison')
        generate_visualizations(model_level_json, class_level_json)


def interface_for_comparing_uml():
    option = st.selectbox(
        "Sample Model Pairs",
        ("Choose", "sample"),
    )
    sample_ground_truth = "Input EMFATIC Ground Truth Model"
    sample_predicted_model = "Input EMFATIC Predicted Truth Model"
    if option != "Choose":
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
    st.subheader("Configuration Panel", divider=True)
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
        config_file_path = f'config_user_{datetime.now(timezone.utc).timestamp()}.json'
        with open(config_file_path, "w") as outfile:
            json.dump(config, outfile, indent=4)
        ground_truth_path = f'ground_truth_model{datetime.now(timezone.utc).timestamp()}.uml'
        predicted_path = f'predicted_model{datetime.now(timezone.utc).timestamp()}.uml'
        with open(config_file_path, 'w') as fr:
            fr.write(str(config))  
        with open(ground_truth_path, 'w') as fr:
            fr.write(str(ground_truth_model))  
        with open(predicted_path, 'w') as fr:
            fr.write(str(predicted_model))  
            
        model_level_json, class_level_json = Adapter.compare_uml2_models_syntactically_and_semantically(ground_truth_path, predicted_path, config_file_path)
                
        st.write(f'Took {model_level_json["time in milliseconds for syntantic comparison"]} for sytnactic comparison')
        generate_visualizations(model_level_json, class_level_json)

def interface_for_bulk_ecore():
    help = st.toggle("View Instructions")
    if help:
        folder_structure = '''You are here because you want to compare more than 1 model pairs.

        For this to work; you should upload a zip folder that follows the following directory pattern:
        . <folder_name (any string)> 
        | +-- <project_folder_name (any string)> 
        | | +-- <base> 
        | | | +-- <model_name>.(ecore/uml/emf) 
        | | +-- <predicted> 
        | | | +-- <model_name>.(ecore/uml/emf) 
        |
        | +-- <project_folder_name (any string)> 
        | +-- <base> 
        | | +-- <model_name>.(ecore/uml/emf) 
        | +-- <predicted> 
        | | | +-- <model_name>.(ecore/uml/emf)
        '''
        st.markdown(folder_structure)

        with open('sample_zip_folder_for_user.zip', 'rb') as f:
            st.download_button('Download Sample Zip', f.read(), file_name="sample_zip_folder_for_user.zip", type="primary")

    col1, col2 = st.columns(2)
    with col1:
        base_model_type = st.selectbox("Choose Base Model Type", ["ecore", "uml", "emf"])
    with col2:
        predicted_model_type = st.selectbox("Choose Predicted Model Type", ["ecore", "uml", "emf"])
    uploaded_zip_folder = st.file_uploader(
        "Upload a zip file containing ecore model pairs", accept_multiple_files=False
    )
    
    st.subheader("Configuration Panel", divider=True)
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
        pairs_count = 0
        pairs = []
        if uploaded_zip_folder is not None:
            extract_dir = f'extracted_files_{datetime.now(timezone.utc).timestamp()}'
            st.session_state['bulk_extraction_directory'] = extract_dir
            if not os.path.exists(extract_dir):
                os.mkdir(extract_dir)
            
            with zipfile.ZipFile(io.BytesIO(uploaded_zip_folder.read()), 'r') as zip_ref:
                zip_ref.extractall(extract_dir)

            for meta_sub_folder in os.listdir(extract_dir):
                meta_sub_folder_path = os.path.join(extract_dir, meta_sub_folder)
                for subfolder_name in os.listdir(meta_sub_folder_path):
                    subfolder_path = os.path.join(meta_sub_folder_path, subfolder_name)
                    
                    if os.path.isdir(subfolder_path):
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
            st.session_state['model_pairs'] = pairs
            st.success('Zip File Processed. Performing comparison')
        array_of_model_level_json = []
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
        config_file_path = f'config_user_{datetime.now(timezone.utc).timestamp()}.json'
        with open(config_file_path, "w") as outfile:
            json.dump(config, outfile, indent=4)
        for pair in st.session_state['model_pairs']:
            for base_model in pair["base"]:
                for predicted_model in pair["predicted"]:
                    base_model_extension = base_model[base_model.rindex(".") + 1:]
                    predicted_model_extension = predicted_model[predicted_model.rindex(".") + 1:]
                    ground_ecore_model = None
                    predicted_ecore_model = None
                    if base_model_extension == base_model_type:
                        ground_ecore_model = base_model
                    if predicted_model_extension == predicted_model_type:
                        predicted_ecore_model = predicted_model
                    if ground_ecore_model is not None and predicted_ecore_model is not None:
                        try:
                            model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(ground_ecore_model, predicted_ecore_model, config_file_path, remove_artifacts=False)
                            model_level_json["model1_identifier"] = base_model # f'{base_model[:base_model.rindex("/")]}'
                            model_level_json["model2_identifier"] = predicted_model # f'{predicted_model[:predicted_model.rindex("/")]}'
                            st.session_state["model_level_json_map_bulk"][f'{predicted_model[:predicted_model.rindex("/")]}/model_level_json.json'] = model_level_json    
                            st.session_state["class_level_json_map_bulk"][f'{predicted_model[:predicted_model.rindex("/")]}/class_level_json.json'] = class_level_json    
                            array_of_model_level_json.append(model_level_json)
                        except Exception as e:
                            st.write(f'Could not perform comparison for {base_model} with {predicted_model} \n {e}')
        df = summarize_results_of_bulk_comparison(array_of_model_level_json)
        os.remove(config_file_path)
        st.session_state['bulk_models_df'] = df
        st.session_state['bulk_models_loaded'] = True
        shutil.rmtree(st.session_state["bulk_extraction_directory"])
    if st.session_state['bulk_models_loaded']:
        new_df = st.session_state['bulk_models_df']
        st.header("Results CSV view", divider=True)
        st.write(new_df)
        col1, col2 = st.columns([0.9, 0.10])
        with col1:
            st.header("Aggregate Visualizations", divider=True)
            fig = px.box(new_df, y=["comparit_f1_score", "comparit_precision", "comparit_recall", "SEMANTIC_SIMILARITY"], points="all")
            st.plotly_chart(fig)
        with col2:
            pass
        
        values = new_df["predicted_model"].values
        st.header("Individual Pair Visualizations", divider=True)
        option = st.selectbox("Select Model Pair to view individual results", values)
        if option != None:
            model_level_json = {}
            class_level_json = {}
            model_level_json = st.session_state["model_level_json_map_bulk"][f'{option[:option.rindex("/")]}/model_level_json.json']
            class_level_json = st.session_state["class_level_json_map_bulk"][f'{option[:option.rindex("/")]}/class_level_json.json']                
            generate_visualizations(model_level_json, class_level_json)
        
def interface_for_viewing_evaluation_results():
    df = pd.read_csv("evaluation_results.csv")
    st.write("Results")
    st.dataframe(df)

    msqe_syntactic = ((df['expected_f1_score'] - df['comparit_f1_score']) ** 2).mean()
    msqe_semantic = ((df['expected_f1_score'] - df['SEMANTIC_SIMILARITY']) ** 2).mean()
    se_syntactic = sum(abs(df['expected_f1_score'] - df['comparit_f1_score']))
    se_semantic = sum(abs(df['expected_f1_score'] - df['SEMANTIC_SIMILARITY']))
    st.write("Aggregate Results")
    results_summary = pd.DataFrame(columns=["Sum of abs Semantic Error", "Sum of abs Syntactic Error", "Semantic Similarity MSQE", "Sntactic Similarity MSQE"], data = [[se_semantic, se_syntactic, msqe_semantic, msqe_syntactic]]).T
    st.dataframe(results_summary)

    fig = px.box(df, y=["comparit_f1_score", "comparit_precision", "comparit_recall", "SEMANTIC_SIMILARITY"], points="all")
    st.plotly_chart(fig)

    fig = px.box(df, y=["expected_f1_score", "expected_precision", "expected_recall"], points="all")
    st.plotly_chart(fig)

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
            path = option[:option.rindex("/")] + "/base_model.ecore"
            with open(path) as fr:
                base_model = fr.read()
        except Exception as e:
            base_model = "Model not found"
        st.text_area("base_model_ecore", base_model, height = 500)

    with col2:
        path = f'{option}/{option[option.rindex("/"):]}.ecore'
        with open(path) as fr:
            predicted_model = fr.read()
        st.text_area("predicted model ecore", predicted_model, height = 500)

    col1, col2 = st.columns(2)
    with col1:
        base_model = ""
        try:
            path = option[:option.rindex("/")] + "/base_model.emf"
            with open(path) as fr:
                base_model = fr.read()
        except Exception as e:
            base_model = "Model not found"
        st.text_area("base_model_emfatic", base_model, height = 500)

    with col2:
        path = f'{option}/{option[option.rindex("/"):]}.emf'
        with open(path) as fr:
            predicted_model = fr.read()
        st.text_area("predicted model emfatic", predicted_model, height = 500)

    generate_visualizations(model_level_json, class_level_json)

def interface_for_ecore_to_emfatic():
    col1, col2, col3 = st.columns([0.45, 0.10, 0.45])
    emfatic_model = ""
    with col1:
        ecore_model = st.text_area("Enter Ecore Model", CONSTANTS.SAMPLE_ECORE_MODEL_SINGLE.value, height = 500)
    with col2:
        if st.button("Go"):
            model_path = f'model_{datetime.now(timezone.utc).timestamp()}.ecore'
            with open(model_path, 'w') as fr:
                fr.write(str(ecore_model))
            emfatic_path = Adapter.get_emfatic_from_ecore(model_path)
            with open(emfatic_path, 'r') as fr:
                try:
                    emfatic_model = fr.read()
                except Exception as e:
                    emfatic_model = "failed to convert; it is likely that ecore model is invalid"
            os.remove(model_path)
            os.remove(emfatic_path)
    with col3:
        st.text_area("Emfatic Model will appear here", emfatic_model, height = 500)

def interface_for_emfatic_to_ecore():
    col1, col2, col3 = st.columns([0.45, 0.10, 0.45])
    ecore_model = ""
    with col1:
        emfatic_model = st.text_area("Enter Emfatic Model", CONSTANTS.SAMPLE_EMFATIC_MODEL_SINGLE.value ,height = 500)
    with col2:
        if st.button("Go"):
            model_path = f'model_{datetime.now(timezone.utc).timestamp()}.emf'
            with open(model_path, 'w') as fr:
                fr.write(str(emfatic_model))
            ecore_path = Adapter.get_ecore_model_from_emfatic(model_path)
            with open(ecore_path, 'r') as fr:
                try:
                    ecore_model = fr.read()
                except Exception as e:
                    ecore_model = "failed to convert; it is likely that ecore model is invalid"
            os.remove(model_path)
            os.remove(ecore_path)
    with col3:
        st.text_area("Ecore Model will appear here", ecore_model, height = 500)

def interface_for_uml2_to_ecore():
    col1, col2, col3 = st.columns([0.45, 0.10, 0.45])
    ecore_model = ""
    with col1:
        uml_model = st.text_area("Enter UML Model", CONSTANTS.SAMPLE_UML2_MODEL_SINGLE.value, height = 500)
    with col2:
        if st.button("Go"):
            model_path = f'model_{datetime.now(timezone.utc).timestamp()}.uml'
            with open(model_path, 'w') as fr:
                fr.write(str(uml_model))
            uml2_path = Adapter.get_ecore_model_from_uml2(model_path)
            with open(uml2_path, 'r') as fr:
                try:
                    ecore_model = fr.read()
                except Exception as e:
                    ecore_model = "failed to convert; it is likely that ecore model is invalid"
            os.remove(model_path)
            os.remove(uml2_path)
    with col3:
        st.text_area("Ecore Model will appear here", ecore_model, height = 500)

user_choice = st.selectbox("What do you want to do?", ["Choose", "Compare Ecore", "Compare Emfatic", "Compare UML2", "Bulk Comparison", "Ecore to Emfatic", "Emfatic to Ecore", "Uml2 to Ecore", "View Tool Evaluation Results"])

if user_choice == "Compare Ecore":
    interface_for_comparing_ecore()
elif user_choice == "Compare Emfatic":
    interface_for_comparing_emfatic()
elif user_choice == "Compare UML2":
    interface_for_comparing_uml()
elif user_choice == "Bulk Comparison":
    interface_for_bulk_ecore()    
elif user_choice == "View Tool Evaluation Results":
    interface_for_viewing_evaluation_results()
elif user_choice == "Ecore to Emfatic":
    interface_for_ecore_to_emfatic()
elif user_choice == "Emfatic to Ecore":
    interface_for_emfatic_to_ecore()    
elif user_choice == "Uml2 to Ecore":
    interface_for_uml2_to_ecore()    