package com.x12q.randomizer.randomizer.builder

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.*

/**
 * A builder that can build a list of [ClassRandomizer]
 */
class RandomizerListBuilder {

    private var lst = mutableListOf<ClassRandomizer<*>>()

    fun build(): Collection<ClassRandomizer<*>> {
        return lst.toList()
    }

    /**
     * Add a randomizer to this builder.
     */
    fun add(randomizer: ClassRandomizer<*>): RandomizerListBuilder {
        lst.add(randomizer)
        return this
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
     * Add a [Float] randomizer to this builder.
     */
    fun float(random: () -> Float): RandomizerListBuilder {
        lst.add(floatRandomizer(random))
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
     * Add a [Double] randomizer to this builder.
     */
    fun double(random: () -> Double): RandomizerListBuilder {
        lst.add(doubleRandomizer(random))
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
     * Add a [Char] randomizer to this builder.
     */
    fun char(random: () -> Char): RandomizerListBuilder {
        lst.add(charRandomizer(random))
        return this
    }
}
