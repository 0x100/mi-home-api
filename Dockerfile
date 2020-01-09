FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} mi-home-app.jar
ENTRYPOINT ["java","-jar","/mi-home-app.jar"]