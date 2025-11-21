FROM eclipse-temurin:17-jre
ARG JAR_FILE=target/*.jar
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]