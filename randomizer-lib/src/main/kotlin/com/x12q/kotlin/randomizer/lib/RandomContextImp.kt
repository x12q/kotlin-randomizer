package com.x12q.kotlin.randomizer.lib

class RandomContextImp(
    override val randomConfig: RandomConfig,
    val collection: RandomizerCollection,
) : RandomContext, RandomConfig by randomConfig, RandomizerCollection by collection
