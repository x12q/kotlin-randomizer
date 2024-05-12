package com.x12q.randomizer

import com.x12q.randomizer.randomizer.builder.ParamRandomizerListBuilder
import com.x12q.randomizer.randomizer.builder.RandomizerListBuilder
import com.x12q.randomizer.randomizer.config.RandomizerConfig
import kotlin.random.Random

/**
 * Random context simply contains various configuration of each random call.
 * Again, this one is unique for each random call.
 */
interface RandomContext{
    val random: Random
    val randomizerConfig: RandomizerConfig
    val randomizers: RandomizerListBuilder
    val paramRandomizers: ParamRandomizerListBuilder
}
