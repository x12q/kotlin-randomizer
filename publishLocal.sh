#!/bin/bash

## publish to local repo
./gradlew :randomizer-lib:publishToMavenLocal
./gradlew :randomizer-ir-lib:publishToMavenLocal
