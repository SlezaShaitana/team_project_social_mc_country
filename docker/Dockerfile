FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/e myapp.jar
ENTRYPOINT ["java", "-jar", "myapp.jar"]
