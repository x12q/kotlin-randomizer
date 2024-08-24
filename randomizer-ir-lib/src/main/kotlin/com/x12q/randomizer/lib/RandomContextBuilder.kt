package com.x12q.randomizer.lib


interface RandomContextBuilder {
    val randomConfig:RandomConfig
    fun buildContext():RandomContext
    fun add(randomizer: ClassRandomizer<*>): RandomContextBuilder
    fun setRandomConfig(randomConfig: RandomConfig): RandomContextBuilder
    fun getLazyContext():RandomContext

    companion object{

    }
}


