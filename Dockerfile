FROM ubuntu:latest AS build

RUN apt-get update && apt-get install -y maven openjdk-17-jdk
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim
EXPOSE 8080

COPY --from=build /target/todolist-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

