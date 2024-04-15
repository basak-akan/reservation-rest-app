# Build stage
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Ensure gradlew has execute permissions
COPY gradlew .
RUN chmod +x ./gradlew
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Use the Gradle Wrapper to build the application
RUN ./gradlew build --no-daemon

# Package stage
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=builder /app/build/libs/*.jar code-challenge.jar

CMD ["java", "-jar", "code-challenge.jar"]