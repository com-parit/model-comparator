FROM python:3.8-slim

WORKDIR /app

COPY . /app
COPY ./requirements.txt /app

RUN pip install -r requirements.txt

EXPOSE 8501

CMD ["streamlit", "run", "main.py"]