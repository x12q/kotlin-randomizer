package com.x12q.randomizer.randomizer.builder

import com.x12q.randomizer.*
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import com.x12q.randomizer.randomizer.primitive.*

/**
 * A builder that can build a list of [ClassRandomizer].
 * This builder can include 2 kinds of randomizers:
 * - non-contextual randomizers: these host their own randomizing logic, and has no depends on external [RandomContext].
 * - contextual randomizers: this one contains a reference to an external [RandomContext] that it does NOT belong to, and can use components from such context to do its work.
 * In order to add contextual randomizers, [externalContext] must be set before [buildContextualRandomizer] is called, otherwise an exception will be thrown.
 */
class RandomizerListBuilder {

    /**
     * Normal randomizers are those that do not rely on any external context.
     */
    private var normalRandomizers = mutableListOf<ClassRandomizer<*>>()

    /**
     * Contextual randomizers are those that rely on an external context object.
     */
    var contextualRandomizers = mutableListOf<ClassRandomizer<*>>()

    /**
     * This must be set before adding any contextual randomizers, otherwise.
     */
    var externalContext: RandomContext? = null

    fun buildNormalRandomizer(): Collection<ClassRandomizer<*>> {
        return normalRandomizers.toList()
    }

    fun buildContextualRandomizer(): Collection<ClassRandomizer<*>> {
        if (contextualRandomizers.isNotEmpty()) {
            if (externalContext != null) {
                return contextualRandomizers.toList()
            } else {
                throw IllegalStateException("${this::class.simpleName} must have an inner context in order to invoke buildContextualRandomizer")
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
     * Add a randomizer that will use [random] function to generate random instances of type [T].
     */
    inline fun <reified T> randomizerForClass(
        crossinline random: () -> T
    ): RandomizerListBuilder {
        return add(classRandomizer(random))
    }

    fun getContext(): RandomContext {
        val context = externalContext
        if (context == null) {
            throw IllegalStateException("A ${RandomContext::class.simpleName} must be provided when adding a contextual randomizer")
        } else {
            return context
        }
    }

    /**
     * Add a randomizer that generate random [T] using [externalContext].
     */
    inline fun <reified T> randomizerForClass(): RandomizerListBuilder {
        /**
         * The reason why [externalContext] is not checked here is:
         * - At the time of this builder function is called, it is guaranteed that context is not available.
         */
        this.contextualRandomizers.add(
            classRandomizer<T> {
                val generator = RandomGenerator(getContext())
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
     * Add a [Set] randomizer that always returns a fixed [value].
     */
    fun <T> set(value: Set<T>): RandomizerListBuilder {
        normalRandomizers.add(setRandomizer(value))
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
     * Add a [List] randomizer that always returns a fixed [value].
     */
    fun <T> list(value: List<T>): RandomizerListBuilder {
        normalRandomizers.add(listRandomizer(value))
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
     * Add a [Map] randomizer that always returns a fixed [value].
     */
    fun <K, V> map(value: Map<K, V>): RandomizerListBuilder {
        normalRandomizers.add(mapRandomizer(value))
        return this
    }

    /**
     * Add an [Int] randomizer that generate random int by running [random] function
     */
    fun int(random: () -> Int): RandomizerListBuilder {
        normalRandomizers.add(intRandomizer(random))
        return this
    }

    /**
     * Add an [Int] randomizer that returns a fixed [value]
     */
    fun int(value: Int): RandomizerListBuilder {
        normalRandomizers.add(intRandomizer(value))
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
    fun intUntil(until: Int): RandomizerListBuilder {
        normalRandomizers.add(intRandomizerUntil(until))
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
     * Add a [Float] randomizer that always return a fixed [value].
     */
    fun float(value:Float): RandomizerListBuilder {
        normalRandomizers.add(floatRandomizer(value))
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
    fun floatUntil(until: Float): RandomizerListBuilder {
        normalRandomizers.add(floatRandomizerUntil(until))
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
     * Add a [String] randomizer that always returns a fixed [value].
     */
    fun string(value: String): RandomizerListBuilder {
        normalRandomizers.add(stringRandomizer(value))
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
     * Add a [Double] randomizer to this builder.
     */
    fun double(value: Double): RandomizerListBuilder {
        normalRandomizers.add(doubleRandomizer(value))
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
    fun doubleUntil(until: Double): RandomizerListBuilder {
        normalRandomizers.add(doubleRandomizerUntil(until))
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
     * Add a [Byte] randomizer to this builder.
     */
    fun byte(value: Byte): RandomizerListBuilder {
        normalRandomizers.add(byteRandomizer(value))
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
     * Add a [Short] randomizer that always returns a fixed [value].
     */
    fun short(value: Short): RandomizerListBuilder {
        normalRandomizers.add(shortRandomizer(value))
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
     * Add a [Boolean] randomizer that always returns a fixed [value].
     */
    fun boolean(value: Boolean): RandomizerListBuilder {
        normalRandomizers.add(booleanRandomizer(value))
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
     * Add a [Long] randomizer that always returns a fixed [value].
     */
    fun long(value: Long): RandomizerListBuilder {
        normalRandomizers.add(longRandomizer(value))
        return this
    }


    /**
     * Add an [Long] randomizer that generate random int within [range] to this builder.
     */
    fun long(range: LongRange): RandomizerListBuilder {
        normalRandomizers.add(longRandomizer(range))
        return this
    }

    /**
     * Add an [Long] randomizer that generate random integers up to certain value to this builder.
     */
    fun longUntil(until: Long): RandomizerListBuilder {
        normalRandomizers.add(longRandomizerUntil(until))
        return this
    }

    /**
     * Add a [Char] randomizer to this builder.
     */
    fun char(random: () -> Char): RandomizerListBuilder {
        normalRandomizers.add(charRandomizer(random))
        return this
    }

    /**
     * Add a [Char] randomizer that always returns a fixed [value].
     */
    fun char(value: Char): RandomizerListBuilder {
        normalRandomizers.add(charRandomizer(value))
        return this
    }
}
