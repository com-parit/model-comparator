FROM python:3.8-slim

WORKDIR /app

COPY . /app

RUN pip install Flask gunicorn nltk ragas seaborn dataset python-dotenv scikit-learn gensim

EXPOSE 5050

CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:5050", "wsgi:app"]