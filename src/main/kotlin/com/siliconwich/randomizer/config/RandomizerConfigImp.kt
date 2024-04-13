package com.siliconwich.randomizer.config

import com.siliconwich.randomizer.ClassData
import com.siliconwich.randomizer.parameter.ParameterRandomizer

class RandomizerConfigImp : RandomizerConfig {
    override val parameterCustomRandomizerMap: Map<ClassData, ParameterRandomizer<Any>>
        get() = TODO("Not yet implemented")

    override fun addRandomizer(randomizer: ParameterRandomizer<Any>) {
        TODO("Not yet implemented")
    }

    override fun getCustomRandomizer(key: ClassData): ParameterRandomizer<Any>? {
        TODO("Not yet implemented")
    }

    override val floatParamRandomizer: ParameterRandomizer<Float>
        get() = TODO("Not yet implemented")
}
