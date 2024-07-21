FROM maven:3.8.4-openjdk-17-slim AS build


# Add the Maven Wrapper and make it executable
COPY . /build
WORKDIR /build



EXPOSE 8080

RUN mvn clean package

ENTRYPOINT ["java", "-jar", "target/projet-1.jar"]
