package com.x12q.randomizer.lib.randomizer

import kotlin.reflect.KClass

/**
 * A collection of randomizers
 */
interface RandomizerCollection {
    val randomizersMap:Map<KClass<*>,ClassRandomizer<*>>
}
