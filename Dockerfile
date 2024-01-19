# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package

# Package stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/acft-0.0.1-SNAPSHOT.jar"]