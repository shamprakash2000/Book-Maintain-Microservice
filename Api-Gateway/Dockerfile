FROM adoptopenjdk/openjdk11:alpine

COPY target/Api-Gateway-0.0.1-SNAPSHOT.jar ./api-gateway.jar

CMD ["java", "-jar", "api-gateway.jar"]

EXPOSE 8080