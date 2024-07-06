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

    fun nextShort():Short{
        return random.nextInt().toShort()
    }

    fun nextStringUUID():String{
        return randomUUIDStr()
    }

    fun nextUnit():Unit{
        return Unit
    }

    fun nextNumber():Number{
        return listOf(
            random.nextInt(),
            random.nextLong(),
            random.nextFloat(),
            random.nextDouble(),
            nextShort(),
            nextByte(),
        ).random()
    }
}


