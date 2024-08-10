package com.x12q.randomizer.lib


interface RandomContextBuilder {
    fun buildContext():RandomContext
    fun add(randomizer: ClassRandomizer<*>): RandomContextBuilder
    fun setRandomConfig(randomConfig: RandomConfig): RandomContextBuilder
}

