import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.buildConfig)
    kotlin("plugin.serialization")
}

group = libs.versions.groupId.get()
version = libs.versions.version.get()


repositories {
    mavenCentral()
}

kotlin {
    val javaVersion = libs.versions.jvmVersion.get().toInt()
    jvmToolchain(javaVersion)
}

buildConfig{
    buildConfigField("String", "IR_PLUGIN_ID","\"${libs.versions.irPluginId.get()}\"")
}

dependencies {
    implementation(project(":randomizer-lib"))

    implementation(libs.michaelbull.kotlinResult)

    compileOnly(libs.kotlin.compiler.embeddable)
    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    implementation(libs.kotlin.reflect)

    kapt(libs.dagger.compiler)
    implementation(libs.dagger)
    kaptTest(libs.dagger.compiler)

    testImplementation(project(":randomizer-lib"))
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.coroutine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.compile.test)
    testImplementation(libs.kotlin.compiler.embeddable)
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

tasks.test {
    useJUnitPlatform()
}
