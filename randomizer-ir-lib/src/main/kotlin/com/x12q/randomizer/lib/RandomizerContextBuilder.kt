package com.x12q.randomizer.lib


interface RandomizerContextBuilder {
    fun buildContext():RandomContext
    fun add(randomizer: ClassRandomizer<*>): RandomizerContextBuilder
    fun setRandomConfig(randomConfig: RandomConfig): RandomizerContextBuilder
}

