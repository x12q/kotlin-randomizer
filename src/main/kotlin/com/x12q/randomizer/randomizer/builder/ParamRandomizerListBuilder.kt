package com.x12q.randomizer.randomizer.builder

import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.RandomContext
import com.x12q.randomizer.RandomGenerator
import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.primitive.*

/**
 * A builder that can build a list of [ParameterRandomizer]
 */
class ParamRandomizerListBuilder {

    private var normalRandomizers = mutableListOf<ParameterRandomizer<*>>()

    /**
     * Contextual randomizers are those that rely on an external context object.
     */
    var contextualRandomizers = mutableListOf<ParameterRandomizer<*>>()

    /**
     * This must be set before adding any contextual randomizers, otherwise.
     */
    var externalContext: RandomContext? = null


    fun build(): Collection<ParameterRandomizer<*>> {
        return normalRandomizers.toList()
    }

    fun buildContextualRandomizer(): Collection<ParameterRandomizer<*>> {
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

    fun add(randomizer: ParameterRandomizer<*>): ParamRandomizerListBuilder {
        normalRandomizers.add(randomizer)
        return this
    }

    inline fun <reified T> paramRandomizer(
        crossinline condition: (target: ParamInfo) -> Boolean,
        crossinline random: (ParamInfo) -> T,
    ): ParamRandomizerListBuilder {
        return this.add(
            com.x12q.randomizer.randomizer.param.paramRandomizer(
                condition = condition,
                random = random
            )
        )
    }

    /**
     * Create a [ParameterRandomizer] that only check for type match
     */
    inline fun <reified T> paramRandomizer(
        crossinline random: (ParamInfo) -> T,
    ): ParamRandomizerListBuilder {
        return this.add(com.x12q.randomizer.randomizer.param.paramRandomizer(random))
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
     * Create a [ParameterRandomizer] that only check for type match
     */
    inline fun <reified T> paramRandomizer(): ParamRandomizerListBuilder {
        this.contextualRandomizers.add(
            com.x12q.randomizer.randomizer.param.paramRandomizer<T> {
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
    fun <T> set(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Set<T>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(setParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Set] randomizer that always returns a fixed [value].
     */
    fun <T> set(
        condition: (target: ParamInfo) -> Boolean,
        value: Set<T>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(setParamRandomizer(condition, value))
        return this
    }


    /**
     * Add a [Set] randomizer to this builder.
     */
    fun <T> set(
        random: (ParamInfo) -> Set<T>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(setParamRandomizer(random))
        return this
    }

    /**
     * Add a [Set] randomizer that always returns a fixed [value].
     */
    fun <T> set(
        value: Set<T>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(setParamRandomizer(value))
        return this
    }

    /**
     * Add a [List] randomizer to this builder.
     */
    fun <T> list(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> List<T>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(listParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [List] randomizer that always returns a fixed [value].
     */
    fun <T> list(
        condition: (target: ParamInfo) -> Boolean,
        value: List<T>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(listParamRandomizer(condition, value))
        return this
    }

    /**
     * Add a [List] randomizer that always returns a fixed [value].
     */
    fun <T> list(
        value: List<T>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(listParamRandomizer(value))
        return this
    }

    /**
     * Add a [Map] randomizer to this builder.
     */
    fun <K, V> map(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Map<K, V>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(mapParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Map] randomizer that always returns a fixed [value].
     */
    fun <K, V> map(
        condition: (target: ParamInfo) -> Boolean,
        value: Map<K, V>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(mapParamRandomizer(condition, value))
        return this
    }


    /**
     * Add a [Map] randomizer to this builder.
     */
    fun <K, V> map(
        random: (ParamInfo) -> Map<K, V>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(mapParamRandomizer(random))
        return this
    }

    /**
     * Add a [Map] randomizer that always returns a fixed [value].
     */
    fun <K, V> map(
        value: Map<K, V>
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(mapParamRandomizer(value))
        return this
    }


    /**
     * Add an [Int] randomizer to this builder.
     */
    fun int(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Int,
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(intParamRandomizer(condition, random))
        return this
    }

    /**
     * Add an [Int] randomizer that always returns a fixed [value].
     */
    fun int(
        condition: (target: ParamInfo) -> Boolean,
        value: Int,
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(intParamRandomizer(condition, value))
        return this
    }


    /**
     * Add an [Int] randomizer to this builder.
     */
    fun int(
        random: (ParamInfo) -> Int,
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(intParamRandomizer(random))
        return this
    }

    /**
     * Add an [Int] randomizer that always returns a fixed [value].
     */
    fun int(
        value: Int,
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(intParamRandomizer(value))
        return this
    }

    /**
     * Add an [Int] randomizer to this builder.
     */
    fun int(
        range: IntRange,
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(intParamRandomizer(range))
        return this
    }

    /**
     * Add an [Int] randomizer to this builder.
     */
    fun intUntil(until: Int): ParamRandomizerListBuilder {
        normalRandomizers.add(intParamRandomizerUntil(until))
        return this
    }

    /**
     * Add a [Float] randomizer to this builder.
     */
    fun float(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Float
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(floatParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Float] randomizer that always returns a fixed [value].
     */
    fun float(
        condition: (target: ParamInfo) -> Boolean,
        value: Float
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(floatParamRandomizer(condition, value))
        return this
    }


    /**
     * Add a [Float] randomizer to this builder.
     */
    fun float(
        random: (ParamInfo) -> Float
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(floatParamRandomizer(random))
        return this
    }

    /**
     * Add a [Float] randomizer that always returns a fixed [value].
     */
    fun float(
        value: Float
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(floatParamRandomizer(value))
        return this
    }


    /**
     * Add a [Float] randomizer to this builder.
     */
    fun float(from: Float, to: Float): ParamRandomizerListBuilder {
        normalRandomizers.add(floatParamRandomizer(from, to))
        return this
    }

    /**
     * Add a [Float] randomizer to this builder.
     */
    fun floatUntil(until: Float): ParamRandomizerListBuilder {
        normalRandomizers.add(floatParamRandomizerUntil(until))
        return this
    }

    /**
     * Add a [String] randomizer to this builder.
     */
    fun string(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> String
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(stringParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [String] randomizer that always returns a fixed [value].
     */
    fun string(
        condition: (target: ParamInfo) -> Boolean,
        value: String
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(stringParamRandomizer(condition, value))
        return this
    }

    /**
     * Add a [String] randomizer to this builder.
     */
    fun string(
        random: (ParamInfo) -> String
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(stringParamRandomizer(random))
        return this
    }

    /**
     * Add a [String] randomizer that always returns a fixed [value].
     */
    fun string(
        value: String
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(stringParamRandomizer(value))
        return this
    }


    /**
     * Add an uuid [String] randomizer to this builder.
     */
    fun uuidString(): ParamRandomizerListBuilder {
        normalRandomizers.add(uuidStringParamRandomizer())
        return this
    }

    /**
     * Add a [Double] randomizer to this builder.
     */
    fun double(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Double
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(doubleParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Double] randomizer that always returns a fixed [value].
     */
    fun double(
        condition: (target: ParamInfo) -> Boolean,
        value: Double
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(doubleParamRandomizer(condition, value))
        return this
    }

    /**
     * Add a [Double] randomizer to this builder.
     */
    fun double(
        random: (ParamInfo) -> Double
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(doubleParamRandomizer(random))
        return this
    }

    /**
     * Add a [Double] randomizer that always returns a fixed [value].
     */
    fun double(
        value: Double
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(doubleParamRandomizer(value))
        return this
    }

    /**
     * Add a [Double] randomizer to this builder.
     */
    fun double(from: Double, to: Double): ParamRandomizerListBuilder {
        normalRandomizers.add(doubleParamRandomizer(from, to))
        return this
    }

    /**
     * Add a [Double] randomizer to this builder.
     */
    fun doubleUntil(until: Double): ParamRandomizerListBuilder {
        normalRandomizers.add(doubleParamRandomizerUntil(until))
        return this
    }


    /**
     * Add a [Byte] randomizer to this builder.
     */
    fun byte(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Byte
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(byteParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Byte] randomizer that always returns a fixed [value].
     */
    fun byte(
        condition: (target: ParamInfo) -> Boolean,
        value: Byte
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(byteParamRandomizer(condition, value))
        return this
    }

    /**
     * Add a [Byte] randomizer to this builder.
     */
    fun byte(
        random: (ParamInfo) -> Byte
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(byteParamRandomizer(random))
        return this
    }

    /**
     * Add a [Byte] randomizer that always returns a fixed [value].
     */
    fun byte(
        value: Byte
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(byteParamRandomizer(value))
        return this
    }

    /**
     * Add a [Short] randomizer to this builder.
     */
    fun short(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Short
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(shortParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Short] randomizer that always returns a fixed [value].
     */
    fun short(
        condition: (target: ParamInfo) -> Boolean,
        value: Short
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(shortParamRandomizer(condition, value))
        return this
    }

    /**
     * Add a [Short] randomizer to this builder.
     */
    fun short(
        random: (ParamInfo) -> Short
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(shortParamRandomizer(random))
        return this
    }

    /**
     * Add a [Short] randomizer that always returns a fixed [value].
     */
    fun short(
        value: Short
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(shortParamRandomizer(value))
        return this
    }

    /**
     * Add a [Boolean] randomizer to this builder.
     */
    fun boolean(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Boolean
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(booleanParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Boolean] randomizer that always returns a fixed [value].
     */
    fun boolean(
        condition: (target: ParamInfo) -> Boolean,
        value: Boolean
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(booleanParamRandomizer(condition, value))
        return this
    }

    /**
     * Add a [Boolean] randomizer to this builder.
     */
    fun boolean(
        random: (ParamInfo) -> Boolean
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(booleanParamRandomizer(random))
        return this
    }

    /**
     * Add a [Boolean] randomizer that always returns a fixed [value].
     */
    fun boolean(
        value: Boolean
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(booleanParamRandomizer(value))
        return this
    }

    /**
     * Add a [Long] randomizer to this builder.
     */
    fun long(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Long
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(longParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Long] randomizer that always returns a fixed [value].
     */
    fun long(
        condition: (target: ParamInfo) -> Boolean,
        value: Long
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(longParamRandomizer(condition, value))
        return this
    }


    /**
     * Add a [Long] randomizer to this builder.
     */
    fun long(
        random: (ParamInfo) -> Long
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(longParamRandomizer(random))
        return this
    }

    /**
     * Add a [Long] randomizer that always returns a fixed [value].
     */
    fun long(
        value: Long
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(longParamRandomizer(value))
        return this
    }

    /**
     * Add a [Long] randomizer to this builder.
     */
    fun long(
        longRange: LongRange
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(longParamRandomizer(longRange))
        return this
    }

    /**
     * Add a [Long] randomizer to this builder.
     */
    fun longUntil(until: Long): ParamRandomizerListBuilder {
        normalRandomizers.add(longParamRandomizer(until))
        return this
    }

    /**
     * Add a [Char] randomizer to this builder.
     */
    fun char(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Char
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(charParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Char] randomizer that always returns a fixed [value].
     */
    fun char(
        condition: (target: ParamInfo) -> Boolean,
        value: Char
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(charParamRandomizer(condition, value))
        return this
    }

    /**
     * Add a [Char] randomizer to this builder.
     */
    fun char(
        random: (ParamInfo) -> Char
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(charParamRandomizer(random))
        return this
    }

    /**
     * Add a [Char] randomizer that always returns a fixed [value].
     */
    fun char(
        value: Char
    ): ParamRandomizerListBuilder {
        normalRandomizers.add(charParamRandomizer(value))
        return this
    }
}
