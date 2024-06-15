import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
//    alias(libs.plugins.anvil)
    alias(libs.plugins.buildConfig)
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

    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.24")
    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    implementation(project(":randomizer-lib"))
    implementation(libs.michaelbull.kotlinResult)
    implementation(libs.kotlin.reflect)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(project(":randomizer-lib"))
    implementation(libs.kotlin.coroutine)

    kaptTest(libs.dagger.compiler)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.compile.test)
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.24")

}

tasks.test {
    useJUnitPlatform()
}
