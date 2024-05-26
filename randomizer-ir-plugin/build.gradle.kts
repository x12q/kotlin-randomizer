plugins {
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.anvil)
    alias(libs.plugins.buildConfig)
}

group = libs.versions.groupId.get()
version = libs.versions.version.get()

repositories {
    mavenCentral()
//    google()
}
kotlin {
    val javaVersion = libs.versions.jvmVersion.get().toInt()
    jvmToolchain(javaVersion)
}
buildConfig{
    buildConfigField("String", "IR_PLUGIN_ID","\"${libs.versions.irPluginId.get()}\"")
}
dependencies {

    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    implementation(project(":randomizer-lib"))

    implementation(libs.michaelbull.kotlinResult)
    implementation(libs.kotlin.reflect)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    kaptTest(libs.dagger.compiler)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.compile.test)

}

tasks.test {
    useJUnitPlatform()
}
