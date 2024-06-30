package com.x12q.randomizer

import com.x12q.randomizer.util.randomUUIDStr
import kotlin.random.Random

interface RandomConfig {

    val random: Random get() = Random

    val collectionSizeRange: IntRange get() = 5..5

    fun nextString(): String {
        return randomUUIDStr()
    }

    fun nextInt(): Int {
        return random.nextInt()
    }
}


