package com.x12q.randomizer.randomizer.builder

import com.x12q.randomizer.randomizer.ClassRandomizer

/**
 * Convenient function that invoke [RandomizerListBuilder] to build a list of [ClassRandomizer]
 */
fun randomizers(
    listBuilder: RandomizerListBuilder.()->Unit
):Collection<ClassRandomizer<*>>{
    val builder = RandomizerListBuilder()
    listBuilder(builder)
    return builder.build()
}

fun randomizersBuilder(
    listBuilder: RandomizerListBuilder.()->Unit
):RandomizerListBuilder{
    val builder = RandomizerListBuilder()
    listBuilder(builder)
    return builder
}
