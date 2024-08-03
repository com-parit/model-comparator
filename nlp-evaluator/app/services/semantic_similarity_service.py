import gensim
from datasets import Dataset 
from ragas import evaluate
from ragas.metrics import faithfulness, answer_similarity
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from nltk.tokenize import sent_tokenize, word_tokenize

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
                'question': ['How should the ecore metamodel representation for this java project look like?'],
                'answer': [generated_model],
                'contexts' : [['Extract metamodel as an ecore meta model to capture the java project']],
                'ground_truth': [original_model]
            }

            dataset = Dataset.from_dict(data_samples)

            score = evaluate(dataset,metrics=[faithfulness,answer_similarity])
            return score
        except Exception as e:
            print("could not compute ragas similarity")
            print(e)
            return None
            
    def compare_emfatic_files(self, emf_original_s, emf_predicted_s):
        emf_original_text = emf_original_s.replace("\n", " ")
        emf_original_tokens = self.get_tokens(emf_original_text)

        emf_predicted_text = emf_predicted_s.replace("\n", " ")
        emf_predicted_tokens = self.get_tokens(emf_predicted_text)

        # Compute cosine similarity with tfidf
        cosine_similarity_tfidf = self.compute_tfidf_cosine_similarity(emf_original_text, emf_predicted_text)
        print("Cosine Similarity (tfidf):", cosine_similarity_tfidf)

        # Compute cosine similarity with word2vec
        model = gensim.models.Word2Vec(emf_original_tokens + emf_predicted_tokens, min_count=1, vector_size=100,
                                        window=5,)
        cosine_similarity_word2vec = self.compute_word2vec_cosine_similarity(emf_original_text, emf_predicted_text, model)
        print("Cosine Similarity (w2v):", cosine_similarity_word2vec)

        # Compute ragas similarity
        ragas_similarity = self.compute_ragas_similarity(emf_original_s, emf_predicted_s)        
        result_object = {
            "cosine_similarity_tfidf": float(cosine_similarity_tfidf),
            "cosine_similarity_word2vec": float(cosine_similarity_word2vec),
            "ragas_similarity": float(ragas_similarity) if ragas_similarity else None
        }
        return result_object