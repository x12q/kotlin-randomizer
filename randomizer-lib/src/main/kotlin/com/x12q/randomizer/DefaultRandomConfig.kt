package com.x12q.randomizer

import com.x12q.randomizer.util.randomUUIDStr
import kotlin.random.Random

class DefaultRandomConfig(
    override val random: Random,
    override val collectionSizeRange: IntRange,
    override val charRange: CharRange
) : RandomConfig {

    companion object{
        val default = DefaultRandomConfig(Random,5 .. 5, 'A' .. 'z')
        fun default(
            random: Random = Random,
            collectionSizeRange: IntRange = 0 .. 10,
            charRange: CharRange = 'A' .. 'z'
        ):DefaultRandomConfig{
            return DefaultRandomConfig(random, collectionSizeRange, charRange)
        }
    }
}
