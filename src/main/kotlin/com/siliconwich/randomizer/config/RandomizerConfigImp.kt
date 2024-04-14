package com.siliconwich.randomizer.config

import com.siliconwich.randomizer.RDClassData
import com.siliconwich.randomizer.parameter.ParameterRandomizer

class RandomizerConfigImp : RandomizerCollection {

    private val parameterCustomRandomizerMMap: Map<RDClassData, List<ParameterRandomizer<Any>>> = mutableMapOf()

    override val parameterCustomRandomizerMap: Map<RDClassData, List<ParameterRandomizer<Any>>> = parameterCustomRandomizerMMap

    override fun addCustomRandomizer(randomizer: ParameterRandomizer<Any>) {
        TODO("Not yet implemented")
    }

    override fun getCustomRandomizer(key: RDClassData): List<ParameterRandomizer<Any>> {
        TODO("Not yet implemented")
    }

    override val floatParamRandomizer: ParameterRandomizer<Float>
        get() = TODO("Not yet implemented")
}
