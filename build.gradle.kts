plugins {
    val ktVersion = libs.versions.kotlin.get()
    kotlin("jvm") version ktVersion
}

group = libs.versions.groupName.get()
version = libs.versions.version.get()

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.michaelbull.kotlinResult)
    testImplementation(libs.kotest.assertions.core)
    implementation(libs.kotlin.reflect)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}
