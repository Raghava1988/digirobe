# --- Stage 1: Build the application using Maven ---
# Use an official Maven image that includes a JDK to build our project.
# The 'as builder' gives this stage a name we can reference later.
FROM maven:3.8.5-openjdk-17 AS builder

# Set the working directory inside the image
WORKDIR /app

# Copy the Maven project file first. This layer is cached by Docker,
# so dependencies are not re-downloaded unless pom.xml changes.
COPY pom.xml .

# Copy the rest of the source code
COPY src ./src

# Run the Maven package command to build the application JAR file.
# '-DskipTests' is used to speed up the build process by skipping tests.
RUN mvn clean package -DskipTests


# --- Stage 2: Create the final, lightweight runtime image ---
# Use an official OpenJDK image, which is much smaller than the Maven image.
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the 'builder' stage into this new image.
# We get the exact JAR name from our pom.xml (artifactId-version.jar)
COPY --from=builder /app/target/digirobe-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port that the application will run on.
# Our application.properties sets this to 8080.
EXPOSE 8080

# The command to run when the container starts.
# This executes our Spring Boot application.
ENTRYPOINT ["java", "-jar", "app.jar"]
