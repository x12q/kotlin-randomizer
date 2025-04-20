plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp.devtool)
}

group = "com.x12q"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":randomizer-lib"))
    ksp(project(":randomizer-ksp-processor"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    val javaVersion = libs.versions.jvmVersion.get().toInt()
    jvmToolchain(javaVersion)
}
