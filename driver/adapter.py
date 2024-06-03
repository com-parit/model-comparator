import requests

class Adapter:
	def get_yamlt_comparator_results(ground_truth_ecore_model, predicted_ecore_model):
		yamtl_comparator_url = "http://localhost:8080/compare"
		response = requests.post(
			yamtl_comparator_url, files={"groundTruthModel": ground_truth_ecore_model, "predictedModel": predicted_ecore_model}, 
			data={
    			"projectName": "tech1"
			}
		)
		return response.json()
	
	def get_emfatic(ecore_model):
		yamtl_comparator_url = "http://localhost:8080/emfatic"
		response = requests.post(
			yamtl_comparator_url, files={"ecoreModel": ecore_model})
		return response.text

	def get_emfatic_comparison(groundTruthModelEmfatic, predictedModelEmfatic):
		nlp_comparator_url = "http://localhost:4040/nlp-compare"
		response = requests.post(
			nlp_comparator_url,
			headers={'Content-Type': 'application/json'},
			json={"groundTruthModelEmfatic": groundTruthModelEmfatic, "predictedModelEmfatic": predictedModelEmfatic}
		)
		return response.text
