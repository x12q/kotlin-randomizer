package com.x12q.randomizer.randomizer.builder

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.primitive.*

/**
 * A builder that can build a list of [ParameterRandomizer]
 */
class ParamRandomizerListBuilder {

    private var lst = mutableListOf<ParameterRandomizer<*>>()

    fun build(): Collection<ParameterRandomizer<*>> {
        return lst.toList()
    }

    fun add(randomizer: ParameterRandomizer<*>): ParamRandomizerListBuilder {
        lst.add(randomizer)
        return this
    }

    /**
     * Add a [Set] randomizer to this builder.
     */
    fun <T> set(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Set<T>
    ): ParamRandomizerListBuilder {
        lst.add(setParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [List] randomizer to this builder.
     */
    fun <T> list(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> List<T>
    ): ParamRandomizerListBuilder {
        lst.add(listParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Map] randomizer to this builder.
     */
    fun <K, V> map(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Map<K, V>
    ): ParamRandomizerListBuilder {
        lst.add(mapParamRandomizer(condition, random))
        return this
    }

    /**
     * Add an [Int] randomizer to this builder.
     */
    fun int(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Int,
    ): ParamRandomizerListBuilder {
        lst.add(intParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Float] randomizer to this builder.
     */
    fun float(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Float
    ): ParamRandomizerListBuilder {
        lst.add(floatParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [String] randomizer to this builder.
     */
    fun string(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> String
    ): ParamRandomizerListBuilder {
        lst.add(stringParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Double] randomizer to this builder.
     */
    fun double(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Double
    ): ParamRandomizerListBuilder {
        lst.add(doubleParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Byte] randomizer to this builder.
     */
    fun byte(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Byte
    ): ParamRandomizerListBuilder {
        lst.add(byteParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Short] randomizer to this builder.
     */
    fun short(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Short
    ): ParamRandomizerListBuilder {
        lst.add(shortParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Boolean] randomizer to this builder.
     */
    fun boolean(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Boolean
    ): ParamRandomizerListBuilder {
        lst.add(booleanParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Long] randomizer to this builder.
     */
    fun long(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Long
    ): ParamRandomizerListBuilder {
        lst.add(longParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a [Char] randomizer to this builder.
     */
    fun char(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Char
    ): ParamRandomizerListBuilder {
        lst.add(charParamRandomizer(condition, random))
        return this
    }
}
