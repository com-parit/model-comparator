import gensim
from datasets import Dataset 
from ragas import evaluate
from ragas.metrics import faithfulness, answer_similarity
import re
from gensim.models import Word2Vec
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from gensim.models import KeyedVectors
from nltk.tokenize import sent_tokenize, word_tokenize
from gensim.models import Word2Vec
from gensim.test.utils import common_texts
word_vectors = KeyedVectors.load_word2vec_format('archive/glove.6B/glove.6B.50d.txt', no_header=True)
# word_vectors = KeyedVectors.load_word2vec_format('archive/GoogleNews-vectors-negative300.bin', binary=True)

class SemanticSimilarity:
    def get_tokens(self, text):
        data = []
        for sentence in sent_tokenize(text):
            temp = []
            for word in word_tokenize(sentence):
                temp.append(word.lower())
            data.append(temp)
        return data

    def preprocess_text(self, text):
        return " ".join(text)

    def extract_comments_and_names(self, code):
        tokens = self.get_tokens(code)
        flattened_tokens = [word for sublist in tokens for word in sublist]
        return flattened_tokens

    def get_weighted_embedding(self, doc, tfidf_weights):
        words = doc.split()
        embedding_sum = np.zeros(word_vectors.vector_size)
        valid_word_count = 0 
        for word in words:
            if word in word_vectors and word in tfidf_weights:
                # Multiply TF-IDF weight with the word embedding
                weighted_embedding = tfidf_weights[word] * word_vectors[word]
                embedding_sum += weighted_embedding
                valid_word_count += 1 
        if valid_word_count == 0:
            return np.zeros(word_vectors.vector_size)
        return embedding_sum / valid_word_count
              
    def compare_emfatic_files(self, emf_original_s, emf_predicted_s):
        emf_original_s = re.sub(r'package\s*\w+', '', emf_original_s)
        emf_predicted_s = re.sub(r'package\s*\w+', '', emf_predicted_s)
        emf_original_s = re.sub(r'@namespace.*?\)', '', emf_original_s)
        emf_predicted_s = re.sub(r'@namespace.*?\)', '', emf_predicted_s)
        text1 = self.extract_comments_and_names(emf_original_s)
        text2 = self.extract_comments_and_names(emf_predicted_s)

        doc1 = self.preprocess_text(text1)
        doc2 = self.preprocess_text(text2)

        tfidf_vectorizer = TfidfVectorizer()
        tfidf_matrix = tfidf_vectorizer.fit_transform([doc1, doc2])
        feature_names = tfidf_vectorizer.get_feature_names_out()

        tfidf_weights = []
        for doc_idx in range(tfidf_matrix.shape[0]):
            weights = {feature_names[i]: tfidf_matrix[doc_idx, i] for i in range(len(feature_names))}
            tfidf_weights.append(weights)

        doc1_embedding = self.get_weighted_embedding(doc1, tfidf_weights[0])
        doc2_embedding = self.get_weighted_embedding(doc2, tfidf_weights[1])

        similarity_score = cosine_similarity([doc1_embedding], [doc2_embedding])[0][0]

        print(f"Hybrid semantic similarity: {similarity_score}")
        result_object = {
            "semantic_similarity": float(similarity_score),
        }
        return result_object