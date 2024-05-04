package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer

class ParamRandomizerListBuilder {
    private var lst = mutableListOf<ParameterRandomizer<*>>()

    fun build(): Collection<ParameterRandomizer<*>> {
        return lst.toList()
    }

    fun add(randomizer: ParameterRandomizer<*>): ParamRandomizerListBuilder {
        lst.add(randomizer)
        return this
    }

    fun int(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Int,
    ): ParamRandomizerListBuilder {
        lst.add(intParamRandomizer(condition, random))
        return this
    }

    fun float(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Float
    ): ParamRandomizerListBuilder {
        lst.add(floatParamRandomizer(condition, random))
        return this
    }

    fun string(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> String
    ): ParamRandomizerListBuilder {
        lst.add(stringParamRandomizer(condition, random))
        return this
    }

    fun double(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Double
    ): ParamRandomizerListBuilder {
        lst.add(doubleParamRandomizer(condition, random))
        return this
    }

    fun byte(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Byte
    ): ParamRandomizerListBuilder {
        lst.add(byteParamRandomizer(condition, random))
        return this
    }

    fun short(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Short
    ): ParamRandomizerListBuilder {
        lst.add(shortParamRandomizer(condition, random))
        return this
    }

    fun boolean(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Boolean
    ): ParamRandomizerListBuilder {
        lst.add(booleanParamRandomizer(condition, random))
        return this
    }

    fun long(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Long
    ): ParamRandomizerListBuilder {
        lst.add(longParamRandomizer(condition, random))
        return this
    }

    fun char(
        condition: (target: ParamInfo) -> Boolean,
        random: (ParamInfo) -> Char
    ): ParamRandomizerListBuilder {
        lst.add(charParamRandomizer(condition, random))
        return this
    }
}
