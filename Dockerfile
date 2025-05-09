FROM ubuntu:latest
LABEL authors="WIN 11"

ENTRYPOINT ["top", "-b"]
# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Run stage
FROM openjdk:17
COPY --from=build /app/target/Final-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 5000
CMD ["java", "-jar", "/app/app.jar"]
