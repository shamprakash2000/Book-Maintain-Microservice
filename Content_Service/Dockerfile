FROM adoptopenjdk/openjdk11:alpine

COPY target/Content_Service-0.0.1-SNAPSHOT.jar ./content-service.jar

CMD ["java", "-jar", "content-service.jar"]

EXPOSE 8082