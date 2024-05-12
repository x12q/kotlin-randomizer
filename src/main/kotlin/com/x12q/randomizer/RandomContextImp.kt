package com.x12q.randomizer

import com.x12q.randomizer.randomizer.builder.ParamRandomizerListBuilder
import com.x12q.randomizer.randomizer.builder.RandomizerListBuilder
import com.x12q.randomizer.randomizer.config.RandomizerConfig
import kotlin.random.Random

data class RandomContextImp(
    override val random: Random,
    override val randomizerConfig: RandomizerConfig,
    override val randomizers: RandomizerListBuilder,
    override val paramRandomizers: ParamRandomizerListBuilder,
): RandomContext
