package com.x12q.kotlin.randomizer.lib

class RandomContextImp(
    override val randomConfig: RandomConfig,
    val collection: RandomizerContainer,
) : RandomContext, RandomConfig by randomConfig, RandomizerContainer by collection
