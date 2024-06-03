import requests

yamtl_comparator_url = "http://localhost:8080"
nlp_comparator_url = "http://localhost:4040"
class Adapter:
    def compare_ecore_models(ground_truth_model, predicted_model, projectName):
        comparator_url = "http://localhost:5050/compare"
        with open(ground_truth_model) as groundTruthModel, open(predicted_model) as predictedModel:
            response = requests.post(
				comparator_url,
				files={"groundTruthModel": groundTruthModel, "predictedModel": predictedModel},
				data={
					"projectName": projectName
				})
            return response.json()
    
    def get_ecore_model_from_emfatic(emfaticFilePath):
        comparator_url = f'{yamtl_comparator_url}/emfatic2ecore'
        with open(emfaticFilePath) as emfaticModel:
            ecoreFromEmfaticResponse = requests.post(
                comparator_url,
                files={"emfaticModel":emfaticModel}
            )
        with open(emfaticFilePath.replace(".emf", ".ecore"), "w") as file:
            file.write(ecoreFromEmfaticResponse.text)  
            
    def get_emfatic_from_ecore(ecoreModelFilePath):
        comparator_url = f'{yamtl_comparator_url}/ecore2emfatic'
        with open(ecoreModelFilePath) as emfaticModel:
            ecoreFromEmfaticResponse = requests.post(
                comparator_url,
                files={"ecoreModel":emfaticModel}
            )
        with open(ecoreModelFilePath.replace(".emf", ".ecore"), "w") as file:
            file.write(ecoreFromEmfaticResponse.text)