FROM eclipse-temurin:21-jre
COPY build/libs/message.jar /app/message.jar
WORKDIR /app/
ENTRYPOINT ["java", "-jar", "/app/message.jar"]
