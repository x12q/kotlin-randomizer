plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    val kotlinVersion = "1.9.23"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
}

rootProject.name = "randomizer"

include("randomizer-lib")
include("randomizer-ir-gradle-plugin")
include("randomizer-ir-plugin")
