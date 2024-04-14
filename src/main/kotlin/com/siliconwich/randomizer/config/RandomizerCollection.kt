package com.siliconwich.randomizer.config

import com.siliconwich.randomizer.RDClassData
import com.siliconwich.randomizer.parameter.ParameterRandomizer

/**
 * Configuration for the main Randomizer
 */
interface RandomizerCollection {

    /**
     * A map of custom parameter randomizer
     */
    val parameterCustomRandomizerMap: Map<RDClassData, List<ParameterRandomizer<Any>>>

    fun addCustomRandomizer(randomizer: ParameterRandomizer<Any>)

    fun getCustomRandomizer(key: RDClassData): List<ParameterRandomizer<Any>>?

    val floatParamRandomizer: ParameterRandomizer<Float>
}

