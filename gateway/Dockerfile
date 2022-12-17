FROM amazoncorretto:19-alpine-jdk
COPY /target/*.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]
