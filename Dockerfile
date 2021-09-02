FROM openjdk:8-jdk-alpine
COPY ./target/alumni-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 3000
EXPOSE 9099
ENTRYPOINT ["java","-jar","app.jar"]

