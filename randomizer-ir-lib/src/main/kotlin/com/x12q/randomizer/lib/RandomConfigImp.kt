package com.x12q.randomizer.lib

import kotlin.random.Random

data class RandomConfigImp(
    override val random: Random,
    override val collectionSizeRange: IntRange,
    override val charRange: CharRange,
) : RandomConfig {

    companion object{
        val default = default()
        fun default(
            random: Random = Random,
            collectionSizeRange: IntRange = 0 .. 10,
            charRange: CharRange = 'A' .. 'z',
        ):RandomConfigImp{
            return RandomConfigImp(random, collectionSizeRange, charRange)
        }
    }
}
