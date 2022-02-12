FROM maven:3.8.4-openjdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/Skuddbot.jar /usr/local/lib/Skuddbot.jar
WORKDIR /usr/local/lib
RUN mkdir "GameLogs"

ENTRYPOINT ["java", "-jar", "Skuddbot.jar"]