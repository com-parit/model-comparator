from adapter import Adapter

if __name__ == "__main__":
    base_ecore_model = "./sample_ecore_model_base.ecore"
    predicted_ecore_model = "./sample_ecore_model_predicted.ecore"
    config_file_path = "./config.json"

    # compare ecore models
    model_level_json, class_level_json = Adapter.compare_ecore_models_syntactically_and_semantically(base_ecore_model, predicted_ecore_model, config_file_path, False)

    print("start - Model Comparison Results for ecore models")
    print(model_level_json)
    print(class_level_json)
    print("end - Model Comparison Results for ecore models")