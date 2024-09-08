package com.x12q.randomizer.lib

class RandomContextImp(
    override val randomConfig: RandomConfig,
    val collection: RandomizerCollection2,
) : RandomContext, RandomConfig by randomConfig, RandomizerCollection2 by collection
