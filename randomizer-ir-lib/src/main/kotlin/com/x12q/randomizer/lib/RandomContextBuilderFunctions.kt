package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.constantRandomizer
import com.x12q.randomizer.lib.randomizer.factoryRandomizer


object RandomContextBuilderFunctions {
    inline fun <reified T : Any> RandomContextBuilder.constant(value: T): RandomContextBuilder {
        return add(constantRandomizer(value))
    }

    inline fun <reified T : Any> RandomContextBuilder.constant(makeValue: () -> T): RandomContextBuilder {
        val value = makeValue()
        return constant(value)
    }

    inline fun <reified T : Any> RandomContextBuilder.factory(noinline makeRandom: () -> T): RandomContextBuilder {
        return add(factoryRandomizer(makeRandom))
    }


    fun RandomContextBuilder.int(i: Int): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.int(makeValue: () -> Int): RandomContextBuilder {
        return constant(makeValue)
    }

    // TODO add other convenient functions such as int(), float(), string()...
    fun RandomContextBuilder.float(i: Float): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.float(makeValue: () -> Float): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.double(i: Double): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.double(makeValue: () -> Double): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.string(i: String): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.string(makeValue: () -> String): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.long(i: Long): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.long(makeValue: () -> Long): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.short(i: Short): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.short(makeValue: () -> Short): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.char(i: Char): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.char(makeValue: () -> Char): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.byte(i: Byte): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.byte(makeValue: () -> Byte): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.uInt(i: UInt): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.uInt(makeValue: () -> UInt): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.uLong(i: ULong): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.uLong(makeValue: () -> ULong): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.uByte(i: UByte): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.uByte(makeValue: () -> UByte): RandomContextBuilder {
        return constant(makeValue)
    }

    fun RandomContextBuilder.uShort(i: UShort): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.uShort(makeValue: () -> UShort): RandomContextBuilder {
        return constant(makeValue)
    }
}
