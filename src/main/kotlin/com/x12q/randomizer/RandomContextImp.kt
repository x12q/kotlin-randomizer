package com.x12q.randomizer

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.config.DefaultRandomConfig
import kotlin.random.Random

data class RandomContextImp(
    override val random: Random,
    override val randomizers: Collection<ClassRandomizer<*>>,
    override val paramRandomizers: Collection<ParameterRandomizer<*>>,
    override val defaultRandomConfig: DefaultRandomConfig,
) : RandomConfig
