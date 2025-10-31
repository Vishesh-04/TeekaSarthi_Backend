# =========================================
# STEP 1: Build the application using Maven
# =========================================
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (for build caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Package the Spring Boot app (skip tests for faster build)
RUN mvn clean package -DskipTests

# =========================================
# STEP 2: Run the built Spring Boot application
# =========================================
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file from build stage
COPY --from=build /app/target/teekasarthi-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Add environment variable for active profile (optional)
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
