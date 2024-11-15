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
    imagemagick \  # Install ImageMagick if you want it for image processing
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install Python dependencies (like Pillow for image manipulation)
RUN pip install Pillow  # You can add more dependencies if needed

# Copy the built Java application from the previous stage
COPY --from=build /build/target/projet-1.jar /app/projet-1.jar

# Copy the Python script to the image
COPY transformImg.py /app/transformImg.py

# Set the working directory
WORKDIR /app

# Expose the necessary ports
EXPOSE 8080

# To run both Java and Python applications, you can either:
# 1. Run them together via a process manager like supervisor
# 2. Run them in separate containers (recommended for production)
# For this example, we run Java only as the primary app.

ENTRYPOINT ["java", "-jar", "projet-1.jar"]

# If you want to execute the Python script for image transformations (for example):
# CMD ["python", "transformImg.py", "input.jpg", "/output"]
