package com.x12q.randomizer.lib.randomizer

import kotlin.reflect.KClass


class RandomizerCollectionImp(
    override val randomizersMap: Map<KClass<*>, ClassRandomizer<*>>
) : RandomizerCollection
