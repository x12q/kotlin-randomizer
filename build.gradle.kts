import org.jetbrains.kotlin.gradle.idea.proto.generated.tcs.ideaKotlinProjectCoordinatesProto

plugins {
    val kotlinVersion = libs.versions.kotlin.get()
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    alias(libs.plugins.anvil)
    `maven-publish`
}
val javaVersion = libs.versions.jvmVersion.get().toInt()
group = libs.versions.groupName.get()
version = libs.versions.version.get()
val id ="randomizer"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.michaelbull.kotlinResult)
    testImplementation(libs.kotest.assertions.core)
    implementation(libs.kotlin.reflect)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    kaptTest(libs.dagger.compiler)
    testImplementation(libs.mockk)
    implementation(libs.mockk)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(javaVersion)
    java{
        withSourcesJar()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = id
            version = version
            from(components["java"])
        }
    }
}
