package com.x12q.randomizer.randomizer.builder

import com.x12q.randomizer.random
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import com.x12q.randomizer.randomizer.primitive.*

/**
 * A builder that can build a list of [ClassRandomizer]
 */
class RandomizerListBuilder {

    private var lst = mutableListOf<ClassRandomizer<*>>()

    fun build(): Collection<ClassRandomizer<*>> {
        return lst
    }

    /**
     * Add a [randomizer] to this builder.
     */
    fun add(randomizer: ClassRandomizer<*>): RandomizerListBuilder {
        lst.add(randomizer)
        return this
    }

    /**
     * Add a randomizer that will use [random] function to generate random instances of type [T]
     */
    inline fun <reified T> randomizerForClass(
        crossinline random:()->T
    ): RandomizerListBuilder {
        return add(classRandomizer(random))
    }


    /**
     * Add a [Set] randomizer to this builder.
     */
    fun <T> set(random: () -> Set<T>): RandomizerListBuilder {
        lst.add(setRandomizer(random))
        return this
    }

    /**
     * Add a [List] randomizer to this builder.
     */
    fun <T> list(random: () -> List<T>): RandomizerListBuilder {
        lst.add(listRandomizer(random))
        return this
    }

    /**
     * Add a [Map] randomizer to this builder.
     */
    fun <K, V> map(random: () -> Map<K, V>): RandomizerListBuilder {
        lst.add(mapRandomizer(random))
        return this
    }

    /**
     * Add an [Int] randomizer to this builder.
     */
    fun int(random: () -> Int): RandomizerListBuilder {
        lst.add(intRandomizer(random))
        return this
    }

    /**
     * Add an [Int] randomizer that generate random int within [range] to this builder.
     */
    fun int(range: IntRange): RandomizerListBuilder {
        lst.add(intRandomizer(range))
        return this
    }

    /**
     * Add an [Int] randomizer that generate random integers up to certain value to this builder.
     */
    fun int(until:Int): RandomizerListBuilder {
        lst.add(intRandomizer(until))
        return this
    }

    /**
     * Add a [Float] randomizer to this builder.
     */
    fun float(random: () -> Float): RandomizerListBuilder {
        lst.add(floatRandomizer(random))
        return this
    }

    /**
     * Add a [Float] randomizer that generate random float with a range to this builder.
     */
    fun float(from: Float, to: Float): RandomizerListBuilder {
        lst.add(floatRandomizer(from, to))
        return this
    }

    /**
     * Add a [Float] randomizer that generate random integers up to certain value to this builder.
     */
    fun float(until: Float): RandomizerListBuilder {
        lst.add(floatRandomizer(until))
        return this
    }


    /**
     * Add a [String] randomizer to this builder.
     */
    fun string(random: () -> String): RandomizerListBuilder {
        lst.add(stringRandomizer(random))
        return this
    }

    /**
     * Add an uuid [String] randomizer to this builder.
     */
    fun uuidString():RandomizerListBuilder{
        lst.add(uuidStringRandomizer())
        return this
    }


    /**
     * Add a [Double] randomizer to this builder.
     */
    fun double(random: () -> Double): RandomizerListBuilder {
        lst.add(doubleRandomizer(random))
        return this
    }


    /**
     * Convenient function to create a [ClassRandomizer] that can produce random doubles within a range
     */
    fun double(from:Double, to:Double): RandomizerListBuilder {
        lst.add(doubleRandomizer(from,to))
        return this
    }

    /**
     * Convenient function to create a [ClassRandomizer] that can produce random doubles up to a limit
     */
    fun double(until:Double): RandomizerListBuilder {
        lst.add(doubleRandomizer(until))
        return this
    }


    /**
     * Add a [Byte] randomizer to this builder.
     */
    fun byte(random: () -> Byte): RandomizerListBuilder {
        lst.add(byteRandomizer(random))
        return this
    }

    /**
     * Add a [Short] randomizer to this builder.
     */
    fun short(random: () -> Short): RandomizerListBuilder {
        lst.add(shortRandomizer(random))
        return this
    }

    /**
     * Add a [Boolean] randomizer to this builder.
     */
    fun boolean(random: () -> Boolean): RandomizerListBuilder {
        lst.add(booleanRandomizer(random))
        return this
    }

    /**
     * Add a [Long] randomizer to this builder.
     */
    fun long(random: () -> Long): RandomizerListBuilder {
        lst.add(longRandomizer(random))
        return this
    }

    /**
     * Add an [Long] randomizer that generate random int within [range] to this builder.
     */
    fun long(
        range: LongRange
    ): RandomizerListBuilder {
        lst.add(longRandomizer(range))
        return this
    }

    /**
     * Add an [Long] randomizer that generate random integers up to certain value to this builder.
     */
    fun long(until:Long): RandomizerListBuilder {
        lst.add(longRandomizer(until))
        return this
    }

    /**
     * Add a [Char] randomizer to this builder.
     */
    fun char(random: () -> Char): RandomizerListBuilder {
        lst.add(charRandomizer(random))
        return this
    }
}
