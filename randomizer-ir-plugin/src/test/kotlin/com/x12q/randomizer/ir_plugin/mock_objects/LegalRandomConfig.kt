package com.x12q.randomizer.ir_plugin.mock_objects

import com.x12q.randomizer.RandomConfig
import kotlin.random.Random

class LegalRandomConfig : RandomConfig{
    override val random: Random = Random
    override val collectionSizeRange: IntRange = 5 .. 5

    override fun nextInt(): Int {
        return 1
    }

    override val charRange: CharRange = 'A' .. 'z'
}

