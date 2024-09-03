import os
import shutil
import zipfile
import io
from adapter_ftests import Adapter_Ftests

def process(modelset_directory):
    extract_dir = "./modelset_data"
    ecore_models_path = "./ecore_models_modelset"
    
    if not os.path.exists(ecore_models_path):
        os.mkdir(ecore_models_path)
        
    with open(modelset_directory, 'rb') as f:    
        with zipfile.ZipFile(io.BytesIO(f.read()), 'r') as zip_ref:
            zip_ref.extractall(extract_dir)

    for level1 in os.listdir(f'{extract_dir}/modelset'):
        if (level1 == "raw-data"):
            raw_data = os.path.join(f'{extract_dir}/modelset', level1)
            print(raw_data)
            for level2 in os.listdir(raw_data):
                print(level2)
                if level2 == "repo-ecore-all":
                    repo_ecore_all = os.path.join(raw_data, level2)
                    for level3 in os.listdir(repo_ecore_all):
                        if level3 == "data":
                            projects_path = os.path.join(repo_ecore_all, level3)
                            for project in os.listdir(projects_path):
                                destination = f'{ecore_models_path}/{project}'
                                move_ecore_files_to_root(os.path.join(projects_path, project), destination)                                      
                # if level2 == "repo-genmymodel-uml":
                #     level2_path = os.path.join(raw_data, level2)
                #     for level3 in os.listdir(level2_path):
                #         print(level3)
                #         if level3 == "data":
                #             data_path = os.path.join(level2_path, level3)
                #             for uml_model in os.listdir(data_path):
                #                 print(uml_model)
                #                 uml_model_path = f'{data_path}/{uml_model}'
                #                 destination = f'{ecore_models_path}/{uml_model[:uml_model.rindex(".xmi")]}'
                #                 if not os.path.exists(destination):
                #                     os.mkdir(destination)
                #                 shutil.copy(uml_model_path, destination)
                #                 new_file_path = f'{destination}/{uml_model[0:uml_model.rindex(".xmi")]}.uml'
                #                 os.rename(f'{destination}/{uml_model}', new_file_path)
                #                 try:
                #                     ecore_model_path = Adapter_Ftests.get_ecore_model_from_uml2(new_file_path)
                #                     print(ecore_model_path)
                #                 except Exception as e:
                #                     os.remove(destination)
                #                     print(e)
                                                          
def move_ecore_files_to_root(directory, root_directory):
    # Walk through all subdirectories in the specified directory
    count = 1
    for root, dirs, files in os.walk(directory):
        sub_count = 0
        for file in files:
            if file.endswith('.ecore'):
                # Construct the path to the file
                file_path = os.path.join(root, file)
                # Construct the destination path in the root directory
                destination_path = root_directory + "_" + str(count) + "_" + str(sub_count) + "_" + file[:file.rindex(".ecore")]
                if not os.path.exists(destination_path):
                    os.makedirs(destination_path)
                # Move the file to the root directory
                shutil.copy(file_path, destination_path)
                print(f"Moved: {file_path} -> {destination_path}")
                count = count + 1
                sub_count = sub_count + 1 
modelset_zip_folder = './modelset.zip'

# Call the function
process(modelset_zip_folder)
