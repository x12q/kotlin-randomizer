package com.x12q.randomizer

import com.x12q.randomizer.util.randomUUIDStr
import kotlin.random.Random

object DefaultRandomConfig : RandomConfig {
    override var random: Random = Random
    override var collectionSizeRange: IntRange = 5 .. 5
}
