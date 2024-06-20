FROM maven:3.9.6-eclipse-temurin-22 as builder
ENV TZ="America/Buenos_Aires"
WORKDIR /app
COPY pom.xml .
COPY . .
RUN mvn clean package assembly:single

FROM openjdk:22
WORKDIR /app
COPY --from=builder app/target/*.jar .
EXPOSE 80
ENTRYPOINT ["java", "-jar", "/app/BotTACS-1.1.0-jar-with-dependencies.jar"]