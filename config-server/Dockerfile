FROM openjdk:17-alpine
COPY ./build/libs/*.jar config-server.jar
ENTRYPOINT ["java", "-jar", "config-server.jar"]
