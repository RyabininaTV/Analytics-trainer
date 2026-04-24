import org.jooq.meta.jaxb.Logging
import org.yaml.snakeyaml.Yaml

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.yaml:snakeyaml:2.3")
    }
}

plugins {
    java
    id("io.quarkus")
    alias(libs.plugins.jooq.codegen)
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

group = "com.example"
version = "1.0-SNAPSHOT"

fun readLocalApplicationYaml(): Map<String, Any> {
    val file = rootProject.file("config/application.yaml")

    if (!file.exists()) {
        return emptyMap()
    }

    return file.inputStream().use {
        Yaml().load<Map<String, Any>>(it) ?: emptyMap()
    }
}

@Suppress("UNCHECKED_CAST")
fun Map<String, Any>.getStringByPath(path: String): String? {
    var current: Any? = this

    for (part in path.split(".")) {
        current = (current as? Map<String, Any>)?.get(part)
    }

    return current as? String
}

val localApplicationYaml = readLocalApplicationYaml()

fun configValue(path: String, envName: String): String {
    return localApplicationYaml.getStringByPath(path)
        ?: System.getenv(envName)
        ?: ""
}

fun requireConfigValue(path: String, envName: String): String {
    return configValue(path, envName).takeIf { it.isNotBlank() }
        ?: throw GradleException(
            "Set '$path' in config/application.yaml or environment variable '$envName'"
        )
}

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

    // jOOQ runtime for Quarkus
    implementation(libs.quarkiverse.jooq)

    // jOOQ codegen
    jooqCodegen(libs.postgresql)

    // lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // tests
    testImplementation(libs.quarkus.junit)

    // logs
    implementation(libs.logback.core)
    implementation(libs.logback.classic)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

jooq {
    configuration {
        logging = Logging.WARN

        jdbc {
            driver = "org.postgresql.Driver"

            url = configValue("quarkus.datasource.jdbc.url", "DB_URL")
            user = configValue("quarkus.datasource.username", "DB_USER")
            password = configValue("quarkus.datasource.password", "DB_PASSWORD")
        }

        generator {
            name = "org.jooq.codegen.DefaultGenerator"

            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"

                inputSchema = "public"

                includes = """
                    attempts|
                    attempt_answers|
                    simulators|
                    task_error_items|
                    task_options|
                    tasks|
                    user_progress|
                    users|
                    user_role_enum|
                    user_status_enum|
                    task_type_enum|
                    attempt_status_enum|
                    answer_type_enum
                """.trimIndent().replace("\\s+".toRegex(), "")

                excludes = """
                    databasechangelog|
                    databasechangeloglock
                """.trimIndent().replace("\\s+".toRegex(), "")
            }

            generate {
                isDeprecated = false
                isRecords = true
                isPojos = false
                isDaos = false
                isFluentSetters = true
            }

            target {
                packageName = "com.example.jooq.generated"
                directory = "src/main/generated"
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("src/main/generated")
        }
    }
}

tasks.named("jooqCodegen") {
    doFirst {
        requireConfigValue("quarkus.datasource.jdbc.url", "DB_URL")
        requireConfigValue("quarkus.datasource.username", "DB_USER")
        requireConfigValue("quarkus.datasource.password", "DB_PASSWORD")
    }
}
