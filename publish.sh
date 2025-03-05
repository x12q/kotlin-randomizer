#!/bin/bash
# kotlin version - ktCompileTest
#2.1.0 - 0.7.0
#2.0.21 - 0.6.0
#2.0.20 - 0.5.1
#2.0.10 - 0.5.1
#2.0.0 - 0.5.1

./gradlew :randomizer-lib:publishToMavenCentral --no-configuration-cache
./gradlew :randomizer-ir-plugin:publishToMavenCentral --no-configuration-cache
./gradlew :randomizer-ir-gradle-plugin:publishPlugins
