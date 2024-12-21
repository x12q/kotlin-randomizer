import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.logging.LogLevel
plugins {
    kotlin("jvm") version "2.0.0"
    id("com.x12q.kotlin.randomizer") version "1.0.0-alpha.8"
    kotlin("plugin.serialization") version "2.0.0"
}


group = "com.x12q"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation(kotlin("test"))
    implementation("com.x12q:kotlin-randomizer-ir-lib:1.0.0-alpha.8")
}

randomizer{
    enable = true
}

