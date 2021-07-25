val javaFxVersion = "15.0.1"
val fxmodules = listOf("base", "controls", "fxml", "media", "graphics", "swing")
val platforms = listOf("win", "linux", "mac")

plugins {
    java
    application
    eclipse
    id("org.openjfx.javafxplugin") version "0.0.9" // https://github.com/openjfx/javafx-gradle-plugin
    id("nu.studer.jooq") version "6.0"
}

repositories {
    jcenter()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

javafx {
    version = javaFxVersion
    modules("javafx.base", "javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.swing")
}

dependencies {
    // Google Guava
    implementation("com.google.guava:guava:30.1.1-jre")

    // Apache Commons
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-io:1.3.2")

    // Cross-platform JavaFX modules
    for (mod in fxmodules) {
        for (plat in platforms) {
            implementation("org.openjfx:javafx-${mod}:${javaFxVersion}:${plat}")
        }
    }

    // MySQL Java Connector
    implementation("mysql:mysql-connector-java:8.0.25")
    jooqGenerator("mysql:mysql-connector-java:8.0.25")

    // https://mvnrepository.com/artifact/commons-validator/commons-validator
    implementation("commons-validator:commons-validator:1.7")

    // JUnit Jupiter
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

}

jooq {
    configurations {
        create("main") {  // name of the jOOQ configuration

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = "jdbc:mysql://localhost:3306/utenze"
                    user = "root"
                    password = "BDFC4JKX4hefpJBUqDO1"
                    properties.add(org.jooq.meta.jaxb.Property().withKey("ssl").withValue("true"))
                }
                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "utenze"
                        forcedTypes.addAll(arrayOf(
                                org.jooq.meta.jaxb.ForcedType()
                                        .withName("varchar")
                                        .withIncludeExpression(".*")
                                        .withIncludeTypes("JSONB?"),
                                org.jooq.meta.jaxb.ForcedType()
                                        .withName("varchar")
                                        .withIncludeExpression(".*")
                                        .withIncludeTypes("INET")
                        ).toList())
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "bdproject"
                        directory = "build/generated-src/jooq/main"  // default (can be omitted)
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

application {
    mainClass.set("bdproject.AppLoader")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    // Enables JUnit 5 Jupiter module
    useJUnitPlatform()
}