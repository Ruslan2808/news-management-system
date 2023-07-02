FROM openjdk:17-alpine
COPY ./build/libs/*.jar news-service.jar
ENTRYPOINT ["java", "-jar", "news-service.jar"]
