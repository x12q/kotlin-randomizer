package com.siliconwich.randomizer.config

import com.siliconwich.randomizer.ClassData
import com.siliconwich.randomizer.parameter.ParameterRandomizer

class RandomizerConfigImp : RandomizerConfig {

    private val parameterCustomRandomizerMMap: Map<ClassData, List<ParameterRandomizer<Any>>> = mutableMapOf()

    override val parameterCustomRandomizerMap: Map<ClassData, List<ParameterRandomizer<Any>>> = parameterCustomRandomizerMMap

    override fun addCustomRandomizer(randomizer: ParameterRandomizer<Any>) {
        TODO("Not yet implemented")
    }

    override fun getCustomRandomizer(key: ClassData): List<ParameterRandomizer<Any>> {
        TODO("Not yet implemented")
    }

    override val floatParamRandomizer: ParameterRandomizer<Float>
        get() = TODO("Not yet implemented")
}
