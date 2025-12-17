# Stage 1: Build the application
FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew clean build -x test

# Stage 2: Create the final image
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/shopvista-backend-*.jar app.jar

# Expose the port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
