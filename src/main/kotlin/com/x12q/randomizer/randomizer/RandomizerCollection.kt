package com.x12q.randomizer.randomizer

import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import javax.inject.Inject

/**
 * A collection of [ClassRandomizer] and [ParameterRandomizer]
 */
data class RandomizerCollection(
    val parameterRandomizers: Map<RDClassData, List<ParameterRandomizer<*>>>,
    val randomizers: Map<RDClassData, ClassRandomizer<*>>
) {

    @Inject
    constructor():this(emptyMap(), emptyMap())

    fun addParamRandomizer(vararg newRandomizers: ParameterRandomizer<*>): RandomizerCollection {
        val newMap = newRandomizers.groupBy { it.paramClassData }
        return this.copy(
            parameterRandomizers = parameterRandomizers + newMap
        )
    }

    fun getParamRandomizer(key: RDClassData): List<ParameterRandomizer<*>>? {
        return parameterRandomizers[key]
    }


    fun addRandomizers(vararg newRandomizers: ClassRandomizer<*>): RandomizerCollection {
        return this.copy(
            randomizers = randomizers + newRandomizers.associateBy { it.paramClassData }
        )
    }

    fun getRandomizer(key: RDClassData): ClassRandomizer<*>? {
        return randomizers[key]
    }

}
