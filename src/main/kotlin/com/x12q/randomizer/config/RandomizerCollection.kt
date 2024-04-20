package com.x12q.randomizer.config

import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer

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

