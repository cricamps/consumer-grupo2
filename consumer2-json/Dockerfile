## Etapa 1: build con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

## Etapa 2: imagen liviana solo con el jar
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /data/alertas-json
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
