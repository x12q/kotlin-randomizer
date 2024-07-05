package com.x12q.randomizer

import com.x12q.randomizer.util.randomUUIDStr
import kotlin.random.Random

interface RandomConfig {

    val random: Random

    val collectionSizeRange: IntRange

    fun nextInt(): Int {
        return random.nextInt()
    }

    fun nextByte():Byte{
        return random.nextBytes(1)[0]
    }

    val charRange:CharRange

    fun nextChar():Char{
        return charRange.random(this.random)
    }
}

