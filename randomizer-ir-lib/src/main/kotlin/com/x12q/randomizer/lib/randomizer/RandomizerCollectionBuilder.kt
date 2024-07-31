package com.x12q.randomizer.lib.randomizer


interface RandomizerCollectionBuilder{
    fun build():RandomizerCollection
    fun add(randomizer: ClassRandomizer<*>): RandomizerCollectionBuilder
}

