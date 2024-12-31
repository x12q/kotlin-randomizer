#!/bin/bash

./gradlew :randomizer-lib:publishToMavenCentral --no-configuration-cache
./gradlew :randomizer-ir-plugin:publishToMavenCentral --no-configuration-cache
./gradlew :randomizer-ir-gradle-plugin:publishPlugins
