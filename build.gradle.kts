plugins {
    java
    id("io.quarkus")
}

repositories {
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    // web
    implementation(libs.quarkus.arc)
    implementation(libs.quarkus.rest)
    implementation(libs.quarkus.rest.jackson)

    // database
    implementation(libs.quarkus.jdbc.postgresql)

    // yaml config
    implementation(libs.quarkus.config.yaml)

    // sql dsl
    implementation(libs.quarkiverse.jooq)

    // lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // testing
    testImplementation(libs.quarkus.junit)
    testImplementation(libs.rest.assured)

    //logs
    val logbackVersion = "1.5.25"
    implementation("ch.qos.logback:logback-core:$logbackVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

group = "com.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
