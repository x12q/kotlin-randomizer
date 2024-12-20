package com.x12q.kotlin.randomizer.lib

/**
 * Random context = RandomConfig + RandomizerCollection
 */
interface RandomContext: RandomConfig, RandomizerCollection{
    val randomConfig:RandomConfig
}
