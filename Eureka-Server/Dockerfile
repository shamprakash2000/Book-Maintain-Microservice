FROM adoptopenjdk/openjdk11:alpine

COPY target/Eureka-Server-0.0.1-SNAPSHOT.jar ./eureka-server.jar

CMD ["java", "-jar", "eureka-server.jar"]

EXPOSE 8761