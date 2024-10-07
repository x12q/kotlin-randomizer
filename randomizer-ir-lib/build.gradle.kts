plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
    `maven-publish`
}
group = libs.versions.groupId.get()
version = libs.versions.version.get()


repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.michaelbull.kotlinResult)
    implementation(libs.kotlin.reflect)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    kaptTest(libs.dagger.compiler)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    val javaVersion = libs.versions.jvmVersion.get().toInt()
    jvmToolchain(javaVersion)
}
val v = libs.versions.version.get()
val g = libs.versions.groupId.get()
publishing {
    /**
     * This is for local publishing
     */
    publications {
        create<MavenPublication>("maven") {
            groupId = g
            artifactId = "randomizer-ir-lib"
            version = v
            from(components["java"])
        }
    }
}
