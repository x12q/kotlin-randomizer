package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer

/**
 * This is not meant to be used by end-user
 */
interface RandomContextBuilder {
    val randomConfig:RandomConfig
    fun build():RandomContext
    fun add(randomizer: ClassRandomizer<*>): RandomContextBuilder
    fun setRandomConfig(randomConfig: RandomConfig): RandomContextBuilder
    fun setRandomConfigAndGenerateStandardRandomizers(randomConfig: RandomConfig): RandomContextBuilder
    fun makePrimitiveRandomizers(randomConfig: RandomConfig)
    fun addTier2Randomizer(makeRandomizer:RandomContext.()-> ClassRandomizer<*>):RandomContextBuilder
}


