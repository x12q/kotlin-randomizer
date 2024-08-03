package com.x12q.randomizer.lib

import kotlin.random.Random

class DefaultRandomConfig(
    override val random: Random,
    override val collectionSizeRange: IntRange,
    override val charRange: CharRange
) : RandomConfig {

    companion object{
        val default = DefaultRandomConfig(Random, 5..5, 'A'..'z')
    }
}
