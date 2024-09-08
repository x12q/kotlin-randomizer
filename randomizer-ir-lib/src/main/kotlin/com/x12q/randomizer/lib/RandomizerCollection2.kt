package com.x12q.randomizer.lib

interface RandomizerCollection2{
    val randomizersMap:Map<TypeKey, ClassRandomizer<*>>
    fun add(key:TypeKey, randomizer: ClassRandomizer<*>):RandomizerCollection2
}
