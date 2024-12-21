package com.x12q.kotlin.randomizer.ir_plugin.mock_objects

import kotlin.random.Random

object RandomConfigForTest: StaticTestRandomConfig() {
    override val random: Random = Random(1)

    override val collectionSizeRange: IntRange = 5 .. 5
    override val charRange: CharRange = 'A' .. 'z'
}
