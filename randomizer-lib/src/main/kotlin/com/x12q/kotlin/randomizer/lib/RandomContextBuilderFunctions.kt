package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer
import com.x12q.kotlin.randomizer.lib.randomizer.constantRandomizer
import com.x12q.kotlin.randomizer.lib.randomizer.factoryRandomizer
import kotlin.reflect.KProperty1

/**
 * This includes builder functions for adding custom randomizers to [RandomContextBuilder]
 */
object RandomContextBuilderFunctions {
    inline fun <reified T> RandomContextBuilder.constant(value: T): RandomContextBuilder {
        return add(constantRandomizer(value))
    }

    inline fun <reified T> RandomContextBuilder.constant(noinline makeValue: () -> T): RandomContextBuilder {
        return add(constantRandomizer(makeValue))
    }

    inline fun <reified T > RandomContextBuilder.factory(noinline makeRandom: () -> T): RandomContextBuilder {
        return add(factoryRandomizer(makeRandom))
    }


    // inline fun <reified T, reified E> factory(kProperty1: KProperty1<T,E>, randomizer:()->E):ClassRandomizer<T>{
    //
    // }

    fun RandomContextBuilder.int(i: Int): RandomContextBuilder {
        return constant(i)
    }

    fun RandomContextBuilder.int(makeValue: () -> Int): RandomContextBuilder {
        return constant(makeValue)
    }

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
