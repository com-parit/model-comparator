from flask import Blueprint, request
from services.semantic_similarity_service import SemanticSimilarity

semantic_similarity = Blueprint('semantic_similarity', __name__)

@semantic_similarity.route('/nlp-compare', methods=['POST'])
def post_endpoint():
	data = request.get_json()	
	if "groundTruthModelEmfatic" in data and "predictedModelEmfatic" in data:
		semanticSimilarity = SemanticSimilarity()
		result_object = semanticSimilarity.compare_emfatic_files(data["groundTruthModelEmfatic"], data["predictedModelEmfatic"])
		return result_object, 200
	else:
		return "Models not sent"