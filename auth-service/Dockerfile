FROM openjdk:17-alpine
COPY ./build/libs/*.jar auth-service.jar
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
