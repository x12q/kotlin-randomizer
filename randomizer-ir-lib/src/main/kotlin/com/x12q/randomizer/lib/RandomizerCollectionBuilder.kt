package com.x12q.randomizer.lib


interface RandomizerCollectionBuilder{
    fun build(): RandomizerCollection
    fun add(randomizer: ClassRandomizer<*>): RandomizerCollectionBuilder
}

