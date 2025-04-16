# backend/Dockerfile

FROM eclipse-temurin:21-jdk AS build

# Trust SSL + speed up Gradle (optional if you solved certs)
RUN apt-get update && apt-get install -y ca-certificates

WORKDIR /app
COPY . .
ENV GRADLE_USER_HOME=/root/.gradle

# Make gradlew executable
RUN chmod +x ./gradlew

# Build JAR
RUN ./gradlew build -x test

# ── Runtime Image ─────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
