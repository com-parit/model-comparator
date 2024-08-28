FROM openjdk:17-jdk-alpine

VOLUME /tmp

ARG JAR_FILE=jars/javaEvaluation-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
