package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer

/**
 * A map of data type to their randomizer
 */
interface RandomizerCollection{
    val randomizersMap:Map<TypeKey, ClassRandomizer<*>>
    fun add(key:TypeKey, randomizer: ClassRandomizer<*>):RandomizerCollection
    fun add(randomizer: ClassRandomizer<*>):RandomizerCollection
    fun remove(key: TypeKey):RandomizerCollection
    fun removeAll():RandomizerCollection
}
