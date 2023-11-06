FROM adoptopenjdk/openjdk11:alpine

COPY target/User_Service-0.0.1-SNAPSHOT.jar ./user-service.jar

CMD ["java", "-jar", "user-service.jar"]

EXPOSE 8081