package com.siliconwich.randomizer.config

import com.siliconwich.randomizer.ClassData
import com.siliconwich.randomizer.parameter.ParameterRandomizer

/**
 * Configuration for the main Randomizer
 */
interface RandomizerConfig {

    /**
     * A map of custom parameter randomizer
     */
    val parameterCustomRandomizerMap: Map<ClassData, List<ParameterRandomizer<Any>>>

    fun addCustomRandomizer(randomizer: ParameterRandomizer<Any>)

    fun getCustomRandomizer(key: ClassData): List<ParameterRandomizer<Any>>?

    val floatParamRandomizer: ParameterRandomizer<Float>
}

