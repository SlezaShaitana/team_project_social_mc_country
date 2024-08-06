FROM openjdk:17-oracle
WORKDIR /app
COPY target/mc-country-0.0.1-SNAPSHOT.jar myapp.jar
ENTRYPOINT ["java", "-jar", "myapp.jar"]
