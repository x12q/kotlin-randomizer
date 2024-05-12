import com.vanniktech.maven.publish.SonatypeHost

plugins {
    val kotlinVersion = libs.versions.kotlin.get()
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    alias(libs.plugins.anvil)
    id("com.vanniktech.maven.publish") version "0.28.0"
    `maven-publish`
}

val javaVersion = libs.versions.jvmVersion.get().toInt()
group = libs.versions.groupName.get()
version = libs.versions.version.get()

val id = "randomizer"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.michaelbull.kotlinResult)
    implementation(libs.kotlin.reflect)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.mockk)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    kaptTest(libs.dagger.compiler)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(javaVersion)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(group.toString(), id, version.toString())

    pom{
        name.set("Randomizer")
        description.set("A randomizer library for Kotlin")
        inceptionYear.set("2024")
        url.set("https://github.com/x12q/randomizer")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("x12q")
                name.set("The-Phong Pham")
                url.set("x12q.com")
            }
        }
        scm {
            url.set("https://github.com/x12q/randomizer")
            connection.set("scm:git:git://github.com/x12q/randomizer.git")
            developerConnection.set("scm:git:ssh://git@github.com/x12q/randomizer.git")
        }
        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/x12q/randomizer/issues")
        }
    }
}



//publishing {
//    /**
//     * This is for local publishing
//     */
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = group.toString()
//            artifactId = id
//            version = version
//            from(components["java"])
//        }
//    }
//}
