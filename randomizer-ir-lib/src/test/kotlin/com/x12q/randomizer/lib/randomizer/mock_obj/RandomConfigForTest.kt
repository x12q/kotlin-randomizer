package com.x12q.randomizer.lib.randomizer.mock_obj

import kotlin.random.Random

object RandomConfigForTest: DefaultTestRandomConfig() {
    override val random: Random = Random(1)

    override val collectionSizeRange: IntRange = 5 .. 5
    override val charRange: CharRange = 'A' .. 'z'
}
