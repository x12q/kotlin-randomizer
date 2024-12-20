plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    val kotlinVersion = "2.0.0"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
}

rootProject.name = "kotlin-randomizer"

include("randomizer-ir-gradle-plugin")
include("randomizer-ir-plugin")
include("randomizer-ir-lib")
