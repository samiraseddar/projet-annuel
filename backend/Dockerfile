# Stage 1: Build the Java application using Maven
FROM maven:3.8.4-openjdk-17-slim AS build

# Add the Maven Wrapper and make it executable
COPY . /build
WORKDIR /build

RUN mvn clean package

# Stage 2: Build the final image including Python dependencies
FROM python:3.9-slim AS python

# Install dependencies required for adding Node.js repository
RUN apt-get update && apt-get install -y \
    curl \
    gnupg2 \
    lsb-release \
    ca-certificates \
    openjdk-17-jre-headless \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Add NodeSource repository for Node.js installation
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get update \
    && apt-get install -y nodejs npm \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN pip install Pillow

# Copy the built Java application from the previous stage
COPY --from=build /build/target/projet-1.jar /app/projet-1.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "projet-1.jar"]
