package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.ClassRandomizer

interface RandomizerCollection{
    val randomizersMap:Map<TypeKey, ClassRandomizer<*>>
    fun add(key:TypeKey, randomizer: ClassRandomizer<*>)
}
