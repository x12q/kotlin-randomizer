package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ClassRandomizer

/**
 * Convenient function that invoke [RandomizerListBuilder] to build a list of [ClassRandomizer]
 */
fun randomizers(
    configBuilder:RandomizerListBuilder.()->Unit
):Collection<ClassRandomizer<*>>{
    val builder = RandomizerListBuilder()
    configBuilder(builder)
    return builder.build()
}
