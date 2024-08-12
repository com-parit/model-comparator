from flask import Flask, request, jsonify
from controllers.semantic_similarity_controller import semantic_similarity
import nltk

nltk.download('punkt')
app = Flask(__name__)

app.register_blueprint(semantic_similarity, url_prefix='')

if __name__ == '__main__':
    app.run(debug=True, port=4040)
