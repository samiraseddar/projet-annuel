FROM maven:3.8.4-openjdk-17-slim AS build

# Ajout des packages nécessaires pour installer Python
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
    
# Add the Maven Wrapper and make it executable
COPY . /build
WORKDIR /build



EXPOSE 8080

RUN mvn clean package

ENTRYPOINT ["java", "-jar", "target/projet-1.jar"]
