package com.x12q.randomizer.ir_plugin.mock_objects

import com.x12q.randomizer.lib.ClassRandomizer
import com.x12q.randomizer.lib.RandomConfig
import kotlin.random.Random
import kotlin.reflect.KClass


open class DefaultTestRandomConfig : RandomConfig {
    override val random: Random = Random
    override val collectionSizeRange: IntRange = 5..5
    override val charRange: CharRange = 'A'..'z'

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


    override fun nextStringUUID(): String {
        return "abc-uuid"
    }


    override fun nextUnit() {
        return Unit
    }


    override fun nextNumber(): Number {
        return 21
    }

}
