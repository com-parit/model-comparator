from flask import Flask, request, jsonify
import os
from adapter import Adapter
import json

app = Flask(__name__)
UPLOAD_FOLDER = 'uploads'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

@app.route('/compare', methods=['POST'])
def post_endpoint():
    if 'groundTruthModel' not in request.files:
        return jsonify({'error': 'No Ground Truth Model File'}), 400

    if 'predictedModel' not in request.files:
        return jsonify({'error': 'No Predicted Truth Model File'}), 400

    groundTruthModel = request.files['groundTruthModel']
    predictedModel = request.files['predictedModel']

    if groundTruthModel.filename == '':
        return jsonify({'error': 'No ground truth selected file'}), 400

    if predictedModel.filename == '':
        return jsonify({'error': 'No predicted model selected file'}), 400

    if groundTruthModel and predictedModel:
        groundTruthModelFilePath = os.path.join(app.config['UPLOAD_FOLDER'], groundTruthModel.filename)
        groundTruthModel.save(groundTruthModelFilePath)
        predictedModelFilePath = os.path.join(app.config['UPLOAD_FOLDER'], predictedModel.filename)
        predictedModel.save(predictedModelFilePath)
        groundTruthModelEmfatic = None
        predictedModelEmfatic = None
        result = None
        with open(groundTruthModelFilePath, 'rb') as groundTruthModel, open(predictedModelFilePath, 'rb') as predictedModel:
              groundTruthModelEmfatic = Adapter.get_emfatic(groundTruthModel)
              predictedModelEmfatic = Adapter.get_emfatic(predictedModel)
        with open(groundTruthModelFilePath, 'rb') as groundTruthModel, open(predictedModelFilePath, 'rb') as predictedModel:
              result = Adapter.get_yamlt_comparator_results(groundTruthModel, predictedModel)
		
		
        
        nlp_compare_result = Adapter.get_emfatic_comparison(groundTruthModelEmfatic, predictedModelEmfatic)
        nlp_compare_result = json.loads(nlp_compare_result)
        modelName = list(result["modelLevelJson"].keys())[0]
        result["modelLevelJson"][modelName]["cosine_similarity_tfidf"] = nlp_compare_result['cosine_similarity_tfidf']
        result["modelLevelJson"][modelName]["cosine_similarity_word2vec"] = nlp_compare_result['cosine_similarity_word2vec']
        result["modelLevelJson"][modelName]["ragas_faithfulness"] = nlp_compare_result['ragas_similarity']['faithfulness'] if nlp_compare_result['ragas_similarity'] else -1
        result["modelLevelJson"][modelName]["ragas_answer_similarity"] = nlp_compare_result['ragas_similarity']['answer_similarity'] if nlp_compare_result['ragas_similarity'] else -1
        result["modelLevelJson"][modelName]["semantic_similarity_average"] = (result["modelLevelJson"][modelName]["cosine_similarity_tfidf"] + result["modelLevelJson"][modelName]["cosine_similarity_word2vec"] + result["modelLevelJson"][modelName]["ragas_faithfulness"] + result["modelLevelJson"][modelName]["ragas_answer_similarity"])/4

        return jsonify({
            'message': 'success', 
            'result': result, 
		}), 200

if __name__ == '__main__':
    app.run(debug=True, port=5050)
