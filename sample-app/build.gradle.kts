import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.logging.LogLevel
import org.gradle.kotlin.dsl.invoke

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.x12q.kotlin.randomizer") version "1.0.0-alpha.11"
    kotlin("plugin.serialization") version "2.0.0"
}


group = "com.x12q"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}
kotlin {
    val javaVersion = 8
    jvmToolchain(javaVersion)
}
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation(kotlin("test"))
    implementation("com.x12q:kotlin-randomizer-lib:1.0.0-alpha.11")
}

randomizer{
    enable = true
}

