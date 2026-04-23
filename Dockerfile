FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
RUN echo "quarkusPluginId=io.quarkus" > gradle.properties && \
    echo "quarkusPluginVersion=3.6.0" >> gradle.properties && \
    echo "quarkusPlatformGroupId=io.quarkus.platform" >> gradle.properties && \
    echo "quarkusPlatformArtifactId=quarkus-bom" >> gradle.properties && \
    echo "quarkusPlatformVersion=3.6.0" >> gradle.properties
COPY src ./src
RUN gradle clean build --no-daemon
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/analytics-trainer-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
