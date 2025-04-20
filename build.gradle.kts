plugins {
    val kotlinVersion = libs.versions.kotlin.get()
    kotlin("jvm") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.foojay.resolver.convention) apply false
    alias(libs.plugins.ksp.devtool) apply false
}
