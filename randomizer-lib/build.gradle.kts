import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.vanniktech.mavenPublish)
}
group = libs.versions.groupId.get()
version = libs.versions.version.get()

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.coroutine)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    val javaVersion = libs.versions.jvmVersion.get().toInt()
    jvmToolchain(javaVersion)
}

mavenPublishing {

   group = libs.versions.groupId.get()
   version = libs.versions.version.get()
   val artifactId = libs.versions.libArtifactId.get()

   publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
   signAllPublications()
   coordinates(group.toString(), artifactId, version.toString())

   pom{
       name.set(artifactId)
       description.set("A randomizer library for Kotlin")
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
