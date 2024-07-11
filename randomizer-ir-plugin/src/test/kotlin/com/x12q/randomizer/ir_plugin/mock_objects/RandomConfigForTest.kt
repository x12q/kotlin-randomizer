package com.x12q.randomizer.ir_plugin.mock_objects

import com.x12q.randomizer.RandomConfig
import kotlin.random.Random

object RandomConfigForTest: RandomConfig {
    override val random: Random = Random(1)

    override val collectionSizeRange: IntRange = 5 .. 5
    override val charRange: CharRange = 'A' .. 'z'
}