FROM python:3.8-slim

WORKDIR /app

COPY . /app

RUN pip install -r requirements.txt
RUN pip install gunicorn

EXPOSE 4040

CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:4040", "wsgi:app"]