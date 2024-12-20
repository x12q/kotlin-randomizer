package com.x12q.kotlin.randomizer.lib.test_utils.mock_obj.random_config

import com.x12q.kotlin.randomizer.lib.RandomConfig
import kotlin.random.Random


open class StaticTestRandomConfig: RandomConfig {
    override val random: Random = Random
    override val collectionSizeRange: IntRange = 5 .. 5
    override val charRange: CharRange = 'A' .. 'z'
    override val stringSize: IntRange = 1 .. 20
    override val stringCandidates: List<Char> get()= charRange.toList()

    override fun nextAny(): Any {
        return "any"
    }

    override fun nextFloat(): Float {
        return 1f
    }

    override fun nextDouble(): Double {
        return 2.0
    }

    override fun nextBoolean(): Boolean {
        return true
    }

    override fun nextInt(): Int {
        return 3
    }

    override fun nextLong(): Long {
        return 4L
    }

    override fun nextUInt(): UInt {
        return 5U
    }

    override fun nextULong(): ULong {
        return 9U
    }

    override fun nextByte(): Byte {
        return 13
    }

    override fun nextUByte(): UByte {
        return 15U
    }

    override fun nextChar(): Char {
        return 'z'
    }

    override fun nextShort(): Short {
        return 17
    }


    override fun nextUShort(): UShort {
        return 19U
    }

    override fun nextString(): String {
        return "abc-uuid"
    }

    override fun nextUnit() {
        return Unit
    }

    override fun nextNumber(): Number {
        return 21
    }
}
