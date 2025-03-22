package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer
import com.x12q.kotlin.randomizer.lib.randomizer.CustomRandomizer

/**
 * A map of data type to their randomizer
 */
interface RandomizerContainer{
    val randomizersMap:Map<TypeKey, ClassRandomizer<*>>
    val propertyRandomizersMap:Map<PropertyKey, CustomRandomizer<*>>
    fun add(key:TypeKey, randomizer: ClassRandomizer<*>):RandomizerContainer
    fun add(randomizer: ClassRandomizer<*>):RandomizerContainer
    fun remove(key: TypeKey):RandomizerContainer
    fun removeAll():RandomizerContainer
}
