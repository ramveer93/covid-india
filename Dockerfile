FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} covid-springboot-app.jar
ENTRYPOINT ["java","-jar","/covid-springboot-app.jar"]