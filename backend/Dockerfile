# Stage 1: Build the Java application using Maven
FROM maven:3.8.4-openjdk-17-slim AS build

# Add the Maven Wrapper and make it executable
COPY . /build
WORKDIR /build

RUN mvn clean package

# Stage 2: Build the final image including Python dependencies
FROM python:3.9-slim AS python

# Install Java and other necessary packages
RUN apt-get update && apt-get install -y \
    openjdk-17-jre-headless \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \

RUN apt-get update && apt-get install -y nodejs npm

RUN pip install Pillow

# Copy the built Java application from the previous stage
COPY --from=build /build/target/projet-1.jar /app/projet-1.jar

# Install Python dependencies if you have a requirements.txt
# COPY requirements.txt /app/
# WORKDIR /app
# RUN pip install -r requirements.txt

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "projet-1.jar"]
