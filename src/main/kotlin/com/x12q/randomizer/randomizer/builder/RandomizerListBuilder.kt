package com.x12q.randomizer.randomizer.builder

import com.x12q.randomizer.*
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import com.x12q.randomizer.randomizer.primitive.*

/**
 * A builder that can build a list of [ClassRandomizer].
 */
class RandomizerListBuilder {

    /**
     * Normal randomizers are those that do not rely on any external context
     */
    private var normalRandomizers = mutableListOf<ClassRandomizer<*>>()

    var contextualRandomizers = mutableListOf<ClassRandomizer<*>>()

    var innerContext: RandomContext? = null

    fun buildNormalRandomizer(): Collection<ClassRandomizer<*>> {
        return normalRandomizers.toList()
    }

    fun buildContextualRandomizer(): Collection<ClassRandomizer<*>> {
        if (contextualRandomizers.isNotEmpty()) {
            if (innerContext != null) {
                return contextualRandomizers.toList()
            } else {
                throw IllegalStateException("${this::class.simpleName} must have an inner context")
            }
        } else {
            return emptyList()
        }
    }

    /**
     * Add a [randomizer] to this builder.
     */
    fun add(randomizer: ClassRandomizer<*>): RandomizerListBuilder {
        normalRandomizers.add(randomizer)
        return this
    }

    /**
     * Add a randomizer that will use [random] function to generate random instances of type [T]
     */
    inline fun <reified T> randomizerForClass(
        crossinline random: () -> T
    ): RandomizerListBuilder {
        return add(classRandomizer(random))
    }

    fun getContext(): RandomContext {
        return innerContext!!
    }

    inline fun <reified T> randomizerForClass(): RandomizerListBuilder {
        this.contextualRandomizers.add(
            classRandomizer<T> {
                val context = getContext()
                val generator = RandomGenerator(context)
                val clzzData = RDClassData.from<T>()
                generator.random(clzzData) as T
            }
        )
        return this
    }


    /**
     * Add a [Set] randomizer to this builder.
     */
    fun <T> set(random: () -> Set<T>): RandomizerListBuilder {
        normalRandomizers.add(setRandomizer(random))
        return this
    }

    /**
     * Add a [List] randomizer to this builder.
     */
    fun <T> list(random: () -> List<T>): RandomizerListBuilder {
        normalRandomizers.add(listRandomizer(random))
        return this
    }

    /**
     * Add a [Map] randomizer to this builder.
     */
    fun <K, V> map(random: () -> Map<K, V>): RandomizerListBuilder {
        normalRandomizers.add(mapRandomizer(random))
        return this
    }

    /**
     * Add an [Int] randomizer to this builder.
     */
    fun int(random: () -> Int): RandomizerListBuilder {
        normalRandomizers.add(intRandomizer(random))
        return this
    }

    /**
     * Add an [Int] randomizer that generate random int within [range] to this builder.
     */
    fun int(range: IntRange): RandomizerListBuilder {
        normalRandomizers.add(intRandomizer(range))
        return this
    }

    /**
     * Add an [Int] randomizer that generate random integers up to certain value to this builder.
     */
    fun int(until: Int): RandomizerListBuilder {
        normalRandomizers.add(intRandomizer(until))
        return this
    }

    /**
     * Add a [Float] randomizer to this builder.
     */
    fun float(random: () -> Float): RandomizerListBuilder {
        normalRandomizers.add(floatRandomizer(random))
        return this
    }

    /**
     * Add a [Float] randomizer that generate random float with a range to this builder.
     */
    fun float(from: Float, to: Float): RandomizerListBuilder {
        normalRandomizers.add(floatRandomizer(from, to))
        return this
    }

    /**
     * Add a [Float] randomizer that generate random integers up to certain value to this builder.
     */
    fun float(until: Float): RandomizerListBuilder {
        normalRandomizers.add(floatRandomizer(until))
        return this
    }


    /**
     * Add a [String] randomizer to this builder.
     */
    fun string(random: () -> String): RandomizerListBuilder {
        normalRandomizers.add(stringRandomizer(random))
        return this
    }

    /**
     * Add an uuid [String] randomizer to this builder.
     */
    fun uuidString(): RandomizerListBuilder {
        normalRandomizers.add(uuidStringRandomizer())
        return this
    }


    /**
     * Add a [Double] randomizer to this builder.
     */
    fun double(random: () -> Double): RandomizerListBuilder {
        normalRandomizers.add(doubleRandomizer(random))
        return this
    }


    /**
     * Convenient function to create a [ClassRandomizer] that can produce random doubles within a range
     */
    fun double(from: Double, to: Double): RandomizerListBuilder {
        normalRandomizers.add(doubleRandomizer(from, to))
        return this
    }

    /**
     * Convenient function to create a [ClassRandomizer] that can produce random doubles up to a limit
     */
    fun double(until: Double): RandomizerListBuilder {
        normalRandomizers.add(doubleRandomizer(until))
        return this
    }


    /**
     * Add a [Byte] randomizer to this builder.
     */
    fun byte(random: () -> Byte): RandomizerListBuilder {
        normalRandomizers.add(byteRandomizer(random))
        return this
    }

    /**
     * Add a [Short] randomizer to this builder.
     */
    fun short(random: () -> Short): RandomizerListBuilder {
        normalRandomizers.add(shortRandomizer(random))
        return this
    }

    /**
     * Add a [Boolean] randomizer to this builder.
     */
    fun boolean(random: () -> Boolean): RandomizerListBuilder {
        normalRandomizers.add(booleanRandomizer(random))
        return this
    }

    /**
     * Add a [Long] randomizer to this builder.
     */
    fun long(random: () -> Long): RandomizerListBuilder {
        normalRandomizers.add(longRandomizer(random))
        return this
    }

    /**
     * Add an [Long] randomizer that generate random int within [range] to this builder.
     */
    fun long(
        range: LongRange
    ): RandomizerListBuilder {
        normalRandomizers.add(longRandomizer(range))
        return this
    }

    /**
     * Add an [Long] randomizer that generate random integers up to certain value to this builder.
     */
    fun long(until: Long): RandomizerListBuilder {
        normalRandomizers.add(longRandomizer(until))
        return this
    }

    /**
     * Add a [Char] randomizer to this builder.
     */
    fun char(random: () -> Char): RandomizerListBuilder {
        normalRandomizers.add(charRandomizer(random))
        return this
    }
}
