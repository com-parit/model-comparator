from flask import Flask, request, jsonify
from semantic_similarity import SemanticSimilarity
import nltk

nltk.download('punkt')
app = Flask(__name__)

@app.route('/nlp-compare', methods=['POST'])
def post_endpoint():
	data = request.get_json()	
	if "groundTruthModelEmfatic" in data and "predictedModelEmfatic" in data:
		semanticSimilarity = SemanticSimilarity()
		result_object = semanticSimilarity.compare_emfatic_files(data["groundTruthModelEmfatic"], data["predictedModelEmfatic"])
		return result_object, 200
        
	else:
		return "Models not sent"

if __name__ == '__main__':
    app.run(debug=True, port=4040)
