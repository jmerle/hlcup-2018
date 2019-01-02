import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.3.11"
}

group = "com.jaspervanmerle.hlcup2018"
version = "1.0.0"

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.github.microutils:kotlin-logging:1.6.22")
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("com.mihnita:color-loggers:1.0.5")
    compile("io.ktor:ktor-server-core:1.1.1")
    compile("io.ktor:ktor-server-netty:1.1.1")
    compile("org.xerial:sqlite-jdbc:3.25.2")
    compile("com.google.code.gson:gson:2.8.5")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Jar> {
        manifest {
            attributes(mapOf("Main-Class" to "${project.group}.MainKt"))
        }

        archiveName = "${rootProject.name}.jar"

        from(configurations.compile.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}
