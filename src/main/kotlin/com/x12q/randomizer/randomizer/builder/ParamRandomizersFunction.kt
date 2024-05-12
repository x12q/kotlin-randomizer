package com.x12q.randomizer.randomizer.builder

import com.x12q.randomizer.randomizer.ParameterRandomizer


/**
 * Convenient function that invoke [ParamRandomizerListBuilder] to build a list of [ParameterRandomizer]
 */
fun paramRandomizers(
    listBuilder: ParamRandomizerListBuilder.()->Unit
):Collection<ParameterRandomizer<*>>{
    val builder = ParamRandomizerListBuilder()
    listBuilder(builder)
    return builder.build()
}

fun paramRandomizersBuilder(
    listBuilder: ParamRandomizerListBuilder.()->Unit
):ParamRandomizerListBuilder{
    val builder = ParamRandomizerListBuilder()
    listBuilder(builder)
    return builder
}
