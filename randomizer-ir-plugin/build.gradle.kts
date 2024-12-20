import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.buildConfig)
    kotlin("plugin.serialization")
    alias(libs.plugins.vanniktech.mavenPublish)

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

//
// mavenPublishing {
//
//     group = libs.versions.groupId.get()
//     version = libs.versions.version.get()
//     val artifactId = "kotlin-randomizer-lib"
//
//     publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
//     signAllPublications()
//     coordinates(group.toString(), artifactId, version.toString())
//
//     pom{
//         name.set(artifactId)
//         description.set("A randomizer library for Kotlin")
//         inceptionYear.set("2024")
//         url.set("https://github.com/x12q/kotlin-randomizer")
//         licenses {
//             license {
//                 name.set("The Apache License, Version 2.0")
//                 url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                 distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//             }
//         }
//         developers {
//             developer {
//                 id.set("phong")
//                 name.set("Phong The Pham")
//                 url.set("x12q.com")
//             }
//         }
//         scm {
//             url.set("https://github.com/x12q/randomizer")
//             connection.set("scm:git:git://github.com/x12q/kotlin-randomizer.git")
//             developerConnection.set("scm:git:ssh://git@github.com/x12q/kotlin-randomizer.git")
//         }
//         issueManagement {
//             system.set("GitHub Issues")
//             url.set("https://github.com/x12q/kotlin-randomizer/issues")
//         }
//     }
// }
