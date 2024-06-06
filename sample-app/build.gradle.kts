
plugins {
    kotlin("jvm") version "2.0.0"
    id("com.x12q.randomizer") version "1.0.0-alpha.7"
    kotlin("plugin.serialization") version "2.0.0"
}

group = "com.x12q"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

randomizer{
    enable = true
}
