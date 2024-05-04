package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer


/**
 * Convenient function that invoke [ParamRandomizerListBuilder] to build a list of [ParameterRandomizer]
 */
fun paramRandomizers(
    configBuilder:ParamRandomizerListBuilder.()->Unit
):Collection<ParameterRandomizer<*>>{
    val builder = ParamRandomizerListBuilder()
    configBuilder(builder)
    return builder.build()
}

