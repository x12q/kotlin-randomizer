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
     * Add a randomizer that can generate [Set] to this builder.
     */
    fun <T> set(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Set<T>
    ): ParamRandomizerListBuilder {
        lst.add(setParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [List] to this builder.
     */
    fun <T> list(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> List<T>
    ): ParamRandomizerListBuilder {
        lst.add(listParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Map] to this builder.
     */
    fun <K, V> map(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Map<K, V>
    ): ParamRandomizerListBuilder {
        lst.add(mapParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Int] to this builder.
     */
    fun int(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Int,
    ): ParamRandomizerListBuilder {
        lst.add(intParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Float] to this builder.
     */
    fun float(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Float
    ): ParamRandomizerListBuilder {
        lst.add(floatParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [String] to this builder.
     */
    fun string(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> String
    ): ParamRandomizerListBuilder {
        lst.add(stringParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Double] to this builder.
     */
    fun double(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Double
    ): ParamRandomizerListBuilder {
        lst.add(doubleParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Byte] to this builder.
     */
    fun byte(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Byte
    ): ParamRandomizerListBuilder {
        lst.add(byteParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Short] to this builder.
     */
    fun short(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Short
    ): ParamRandomizerListBuilder {
        lst.add(shortParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Boolean] to this builder.
     */
    fun boolean(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Boolean
    ): ParamRandomizerListBuilder {
        lst.add(booleanParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Long] to this builder.
     */
    fun long(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Long
    ): ParamRandomizerListBuilder {
        lst.add(longParamRandomizer(condition, random))
        return this
    }

    /**
     * Add a randomizer that can generate [Char] to this builder.
     */
    fun char(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Char
    ): ParamRandomizerListBuilder {
        lst.add(charParamRandomizer(condition, random))
        return this
    }
}
