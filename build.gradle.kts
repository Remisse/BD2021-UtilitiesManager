val javaFxVersion = "17"
val fxModules = listOf("base", "controls", "fxml", "media", "graphics")
val platforms = listOf("win", "linux")

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.9" // https://github.com/openjfx/javafx-gradle-plugin
    id("nu.studer.jooq") version "6.0.1"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

javafx {
    version = javaFxVersion
    modules("javafx.base", "javafx.controls", "javafx.fxml", "javafx.graphics")
}

dependencies {
    // Cross-platform JavaFX modules
    for (mod in fxModules) {
        for (plat in platforms) {
            implementation("org.openjfx:javafx-${mod}:${javaFxVersion}:${plat}")
        }
    }

    // MySQL Java Connector
    implementation("mysql:mysql-connector-java:8.0.27")
    jooqGenerator("mysql:mysql-connector-java:8.0.27")

    // Commons Validator
    implementation("commons-validator:commons-validator:1.7")
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
                    password = ""
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
    mainClass.set("bdproject.GUILoader")

    /*
     * mainClassName was deprecated by Gradle, but it is still required by John Engelman's Shadow plugin.
     * A pull request with a fix was already merged, but it hasn't been released yet;
     * see https://github.com/johnrengelman/shadow/issues/609 and https://github.com/johnrengelman/shadow/pull/612
     */
    @Suppress("DEPRECATION")
    mainClassName = mainClass.get()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    // Enables JUnit 5 Jupiter module
    useJUnitPlatform()
}