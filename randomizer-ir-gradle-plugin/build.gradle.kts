plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.buildConfig)
    signing
}
repositories {
    mavenCentral()
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
dependencies {
    implementation(kotlin("gradle-plugin-api"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

buildConfig {
    buildConfigField("String", "IR_PLUGIN_ID", "\"${libs.versions.irPluginId.get()}\"")
    buildConfigField("String", "IR_PLUGIN_GROUP_ID", "\"${group}\"")
    buildConfigField("String", "IR_PLUGIN_ARTIFACT_ID", "\"${libs.versions.irPluginArtifactId.get()}\"")
    buildConfigField("String", "IR_PLUGIN_VERSION", "\"$version\"")
}
tasks.test {
    useJUnitPlatform()
}
gradlePlugin {
    website = "https://github.com/x12q/kotlin-randomizer"
    vcsUrl = "https://github.com/x12q/kotlin-randomizer.git"

    plugins {
        create("kotlin-randomizer-ir-gradle-plugin") {
            id = libs.versions.gradlePluginId.get()
            displayName = "kotlin-randomizer IR Gradle plugin"
            description = "kotlin-randomizer IR Gradle plugin"
            tags=listOf("kotlin", "kotlin-randomizer", "randomizer")
            implementationClass = "com.x12q.kotlin.randomizer.ir_gradle_plugin.RandomizerGradlePlugin"
        }
    }
}
