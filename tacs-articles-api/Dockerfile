FROM maven:3.9.6 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src/
RUN mvn package

FROM tomcat:10.1.24
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/app.war