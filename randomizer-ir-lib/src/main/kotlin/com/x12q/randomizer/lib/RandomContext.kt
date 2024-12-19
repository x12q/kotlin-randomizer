package com.x12q.randomizer.lib

/**
 * Random context = RandomConfig + RandomizerCollection
 */
interface RandomContext: RandomConfig, RandomizerCollection{
    val randomConfig:RandomConfig
}
