FROM openjdk:17-jdk-alpine

VOLUME /tmp

ARG JAR_FILE=./*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
