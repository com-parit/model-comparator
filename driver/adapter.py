import requests
from dotenv import load_dotenv
import os

load_dotenv()
yamtl_comparator_url = os.getenv("yamtl_comparator_url")
nlp_comparator_url = os.getenv("nlp_comparator_url")
class Adapter:
	def get_yamlt_comparator_results(ground_truth_ecore_model, predicted_ecore_model):
		yamtl_comparator_endpoint = f'{yamtl_comparator_url}/compare'
		response = requests.post(
			yamtl_comparator_endpoint, files={"groundTruthModel": ground_truth_ecore_model, "predictedModel": predicted_ecore_model}, 
			data={
    			"projectName": "tech1"
			}
		)
		return response.json()
	
	def get_emfatic(ecore_model):
		yamtl_comparator_endpoint = f'{yamtl_comparator_url}/emfatic'
		response = requests.post(
			yamtl_comparator_endpoint, files={"ecoreModel": ecore_model})
		return response.text

	def get_emfatic_comparison(groundTruthModelEmfatic, predictedModelEmfatic):
		nlp_comparator_endpoint = f'{nlp_comparator_url}/nlp-compare'
		response = requests.post(
			nlp_comparator_endpoint,
			headers={'Content-Type': 'application/json'},
			json={"groundTruthModelEmfatic": groundTruthModelEmfatic, "predictedModelEmfatic": predictedModelEmfatic}
		)
		return response.text
