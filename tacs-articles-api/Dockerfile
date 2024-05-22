FROM maven:3.9.6 as builder
WORKDIR /app
COPY pom.xml .
COPY src/ /app/src/
RUN mvn package -Dmaven.test.skip=true

FROM tomcat:10.1.24
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/app.war
EXPOSE 8080