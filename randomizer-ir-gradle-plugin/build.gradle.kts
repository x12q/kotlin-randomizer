plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.buildConfig)


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
    buildConfigField("String", "IR_PLUGIN_ARTIFACT_ID", "\"randomizer-ir-plugin\"")
    buildConfigField("String", "IR_PLUGIN_VERSION", "\"$version\"")
}
tasks.test {
    useJUnitPlatform()
}
gradlePlugin {
    plugins {
        create("kotlinIrPluginTemplate") {
            id = libs.versions.gradlePluginId.get() //com.x12q.randomizer
            displayName = "Randomizer IR Gradle plugin"
            description = "Randomizer IR Gradle plugin"
            implementationClass = "com.x12q.randomizer.ir_gradle_plugin.RandomizerGradlePlugin"
        }
    }
}
