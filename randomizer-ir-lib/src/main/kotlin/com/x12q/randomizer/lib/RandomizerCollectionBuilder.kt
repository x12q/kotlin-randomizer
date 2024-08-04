package com.x12q.randomizer.lib


interface RandomizerCollectionBuilder {
    fun build(): RandomizerCollection
    fun buildConfig():RandomConfig
    fun add(randomizer: ClassRandomizer<*>): RandomizerCollectionBuilder
    fun setRandomConfig(randomConfig: RandomConfig): RandomizerCollectionBuilder
}

