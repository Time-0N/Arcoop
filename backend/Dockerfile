# backend/Dockerfile

FROM eclipse-temurin:21-jdk AS build

# Trust SSL + speed up Gradle
RUN apt-get update && apt-get install -y ca-certificates

# Set up working dir
WORKDIR /app

# Copy everything incl. pre-downloaded Gradle
COPY . .
COPY .gradle /root/.gradle
ENV GRADLE_USER_HOME=/root/.gradle

# Use the wrapper you just prepared
RUN ./gradlew build -x test

# ─────────────────────────────

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
