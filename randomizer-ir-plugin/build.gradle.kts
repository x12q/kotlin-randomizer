import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlin.serialization)

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


    compileOnly(libs.kotlin.compiler.embeddable)
    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    implementation(libs.kotlin.reflect)

    kapt(libs.dagger.compiler)
    implementation(libs.dagger)
    kaptTest(libs.dagger.compiler)

    testImplementation(project(":randomizer-lib"))
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.compile.test)
    testImplementation(libs.kotlin.compiler.embeddable)
    testImplementation(libs.kotlin.serialization)
    testImplementation(libs.kotlin.serialization.plugin)
}

tasks.test {
    useJUnitPlatform()
}


mavenPublishing {

    group = libs.versions.groupId.get()
    version = libs.versions.version.get()
    val artifactId = libs.versions.irPluginArtifactId.get()

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates(group.toString(), artifactId, version.toString())

    pom{
        name.set(artifactId)
        description.set("Compiler plugin for kotlin-randomizer")
        inceptionYear.set("2024")
        url.set("https://github.com/x12q/kotlin-randomizer")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("phong")
                name.set("Phong The Pham")
                url.set("x12q.com")
            }
        }
        scm {
            url.set("https://github.com/x12q/kotlin-randomizer")
            connection.set("scm:git:git://github.com/x12q/kotlin-randomizer.git")
            developerConnection.set("scm:git:ssh://git@github.com/x12q/kotlin-randomizer.git")
        }
        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/x12q/kotlin-randomizer/issues")
        }
    }
}
