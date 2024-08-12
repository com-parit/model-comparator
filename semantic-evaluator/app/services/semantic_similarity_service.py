import gensim
from datasets import Dataset 
from ragas import evaluate
from ragas.metrics import faithfulness, answer_similarity
import re
import spacy
from gensim.models import Word2Vec
import numpy as np
from numpy import dot
from numpy.linalg import norm
from imblearn.over_sampling import SMOTE
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from nltk.tokenize import sent_tokenize, word_tokenize

nlp = spacy.load('en_core_web_lg')

class SemanticSimilarity:
    def compute_word2vec_cosine_similarity(self, text1, text2, model):
        tokens1 = text1.split()
        tokens2 = text2.split()

        # Filter out tokens that are not in the Word2Vec vocabulary
        tokens1 = [token for token in tokens1 if token in model.wv]
        tokens2 = [token for token in tokens2 if token in model.wv]

        vector1 = sum(model.wv[token] for token in tokens1) / len(tokens1)
        vector2 = sum(model.wv[token] for token in tokens2) / len(tokens2)

        cosine_sim = cosine_similarity([vector1], [vector2])

        return cosine_sim[0][0]

    def compute_tfidf_cosine_similarity(self, text1, text2):
        vectorizer = TfidfVectorizer()
        tfidf_matrix = vectorizer.fit_transform([text1, text2])

        cosine_sim = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])

        return cosine_sim[0][0]

    def get_tokens(self, text):
        data = []
        
        # iterate through each sentence in the file
        for i in sent_tokenize(text):
            temp = []
        
            # tokenize the sentence into words
            for j in word_tokenize(i):
                temp.append(j.lower())
        
            data.append(temp)
        return data

    def compute_ragas_similarity(self, original_model, generated_model):
        try:
            data_samples = {
                'question': ['How similar is this metamodel to the ground truth metamodel?'],
                'answer': [generated_model],
                'contexts' : [['The metamodel is represents the data model of a java project']],
                'ground_truth': [original_model]
            }

            dataset = Dataset.from_dict(data_samples)

            score = evaluate(dataset,metrics=[faithfulness,answer_similarity])
            return score
        except Exception as e:
            print("could not compute ragas similarity")
            print(e)
            return None

    def preprocess_text(self,text):
        doc = nlp(" ".join(text))
        tokens = [token.lemma_ for token in doc if not token.is_stop and not token.is_punct]
        return tokens

    def extract_comments_and_names(self, code):
        comments = re.findall(r'(?:(?:#|//|/\*|\*|<!--|;).*?$)', code, re.MULTILINE)
        names = re.findall(r'def\s+(\w+)|class\s+(\w+)|(\w+)\s*=\s*[^=\n]+|class\s+\w+\s*\(\s*([^)]+)\s*\)', code)
        
        flattened_names = []
        for tuple_num in names: 
            for name in tuple_num:
                if name:
                    flattened_names.append(name)
        
        return comments + flattened_names  
              
    def compare_emfatic_files(self, emf_original_s, emf_predicted_s):
        text1 = self.extract_comments_and_names(emf_original_s)
        text2 = self.extract_comments_and_names(emf_predicted_s)

        tokens1 = self.preprocess_text(text1)
        tokens2 = self.preprocess_text(text2)

        doc1, doc2 = " ".join(tokens1), " ".join(tokens2)

        vectorizer = TfidfVectorizer()
        X = vectorizer.fit_transform([doc1, doc2]).toarray()

        smote = SMOTE(sampling_strategy='auto')
        X_resampled, y_resampled = smote.fit_resample(X, [0, 1])

        tokens1_resampled = [token for token in vectorizer.inverse_transform([X_resampled[0]])[0]]
        tokens2_resampled = [token for token in vectorizer.inverse_transform([X_resampled[1]])[0]]

        sentences = [tokens1_resampled, tokens2_resampled]

        model = Word2Vec(sentences, vector_size=100, window=5, min_count=1, workers=4)

        vector1 = np.mean([model.wv[tok] for tok in tokens1_resampled if tok in model.wv], axis=0)
        vector2 = np.mean([model.wv[tok] for tok in tokens2_resampled if tok in model.wv], axis=0)

        similarity = dot(vector1, vector2) / (norm(vector1) * norm(vector2))
        
        # Compute ragas similarity
        ragas_similarity = self.compute_ragas_similarity(emf_original_s, emf_predicted_s)        
        result_object = {
            "cosine_similarity_SMOTE": float(similarity),
            "cosine_similarity_word2vec": -1,
            "ragas_similarity": ragas_similarity if ragas_similarity else None
        }
        return result_object