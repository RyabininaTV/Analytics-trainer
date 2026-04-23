FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY src ./src
RUN gradle clean build --no-daemon \
    -PquarkusPluginId=io.quarkus \
    -PquarkusPluginVersion=3.6.0 \
    -PquarkusPlatformGroupId=io.quarkus.platform \
    -PquarkusPlatformArtifactId=quarkus-bom \
    -PquarkusPlatformVersion=3.6.0
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/analytics-trainer-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
