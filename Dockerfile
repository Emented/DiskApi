FROM --platform=linux/amd64 openjdk:17-jdk-alpine
ADD target/DiskApi-0.0.1-SNAPSHOT.jar main.jar
ENTRYPOINT ["java", "-jar", "main.jar"]
EXPOSE 80