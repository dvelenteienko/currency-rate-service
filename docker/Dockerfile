FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=currency-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]