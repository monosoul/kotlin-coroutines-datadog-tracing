plugins {
    application
    kotlin("jvm") version "1.8.10"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

val datadogAgent: Configuration by configurations.creating

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    val ktorVersion = "2.2.4"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    val datadogVersion = "1.10.1"
    implementation("com.datadoghq:dd-trace-ot:$datadogVersion")
    datadogAgent("com.datadoghq:dd-java-agent:$datadogVersion")

    implementation("ch.qos.logback:logback-classic:1.4.6")
}

tasks {
    val downloadDatadogAgent by registering(Copy::class) {
        from(datadogAgent)
        into("$buildDir/libs")

        rename(".*dd-java-agent.*\\.jar", "dd-java-agent.jar")
    }

    register<JavaExec>("runTheApp") {
        group = "application"

        dependsOn(classes)
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("dev.monosoul.coroutines.tracing.TheAppKt")
        jvmArgs("-javaagent:${buildDir}/libs/dd-java-agent.jar")

        environment("DD_INTEGRATION_KOTLIN_COROUTINE_EXPERIMENTAL_ENABLED", "true")
        environment("DD_SERVICE", "kotlin-coroutines-datadog-tracing")
        environment("DD_TRACE_DEBUG", "false")
    }

    register<CreateStartScripts>("runTheAppScripts") {
        mainClass.set("dev.monosoul.coroutines.tracing.TheAppKt")
        applicationName = "the-app"
        outputDir = startScripts.get().outputDir
        classpath = startScripts.get().classpath

        defaultJvmOpts = listOf(
            "-javaagent:${buildDir}/libs/dd-java-agent.jar"
        )
    }

    classes {
        dependsOn(downloadDatadogAgent)
    }
}
