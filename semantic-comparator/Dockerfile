FROM python:3.8-slim

WORKDIR /app

COPY app/ /app
COPY ./requirements.txt /app

RUN pip install -r requirements.txt
RUN pip install --upgrade nltk
RUN pip install gunicorn
RUN python -m nltk.downloader punkt

EXPOSE 9090

CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:9090", "wsgi:app"]