package com.x12q.randomizer.lib.randomizer


interface ClassRandomizerCollectionBuilder{
    fun build():ClassRandomizerCollection
    fun add(classRandomizer: ClassRandomizer<*>): ClassRandomizerCollectionBuilder
}

