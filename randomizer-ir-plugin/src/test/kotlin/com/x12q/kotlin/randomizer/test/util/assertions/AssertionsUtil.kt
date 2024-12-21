package com.x12q.kotlin.randomizer.test.util.assertions

fun codeGenAssertions(factory: GeneratedCodeAssertionBuilder.()-> Unit): GeneratedCodeAssertions {
    val builder = GeneratedCodeAssertionBuilder()
    factory(builder)
    return builder.build()
}

fun codeGenAssertions(baseAssertions: GeneratedCodeAssertions,factory: GeneratedCodeAssertionBuilder.()-> Unit): GeneratedCodeAssertions {
    val builder = GeneratedCodeAssertionBuilder(baseAssertions)
    factory(builder)
    return builder.build()
}
