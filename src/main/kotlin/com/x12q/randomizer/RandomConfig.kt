package com.x12q.randomizer

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.config.DefaultRandomConfig
import kotlin.random.Random

/**
 * A random context is an object that contains all the random configuration of a random call.
 * The purpose of random context is to allow passing all of these to function down there....
 */

interface RandomConfig{
    val random: Random
    val randomizers: Collection<ClassRandomizer<*>>
    val paramRandomizers: Collection<ParameterRandomizer<*>>
    val defaultRandomConfig: DefaultRandomConfig
}
