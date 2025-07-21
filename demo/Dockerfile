# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven files for dependency caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV DATABASE_URL=jdbc:postgresql://db:5432/AutonomoWeb
ENV DATABASE_USERNAME=postgres
ENV DATABASE_PASSWORD=postgres

# Run the application
CMD ["java", "-jar", "target/miproyecto-0.0.1-SNAPSHOT.jar"]
