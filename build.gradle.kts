plugins {
    val kotlinVersion = libs.versions.kotlin.get()
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    alias(libs.plugins.anvil)
}
val javaVersion = libs.versions.jvmVersion.get().toInt()
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
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    kaptTest(libs.dagger.compiler)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(javaVersion)
}
