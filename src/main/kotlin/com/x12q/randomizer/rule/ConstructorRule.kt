package com.x12q.randomizer.rule

/**
 * TODO make constructor rules to be able to linked together into a boolean guanlet.
 */
sealed class ConstructorRule {
    data object PrimaryConstructorOnly : ConstructorRule()
    data object PrimaryConstructorOrFirst : ConstructorRule()
}
