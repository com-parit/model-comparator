from adapter import Adapter

if __name__ == "__main__":
    base_model = "./sample_uml_model_base.uml"
    predicted_model = "./sample_uml_model_predicted.uml"
    config_file_path = "./config.json"

    # compare uml models
    model_level_json, class_level_json = Adapter.compare_uml2_models_syntactically_and_semantically(base_model, predicted_model, config_file_path, False)

    print("start - Model Comparison Results for uml models")
    print(model_level_json)
    print(class_level_json)
    print("end - Model Comparison Results for uml models")