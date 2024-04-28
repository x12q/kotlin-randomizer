package com.x12q.randomizer.randomizer

import javax.inject.Inject
import kotlin.reflect.full.isSubclassOf

/**
 * A collection of [ClassRandomizer] and [ParameterRandomizer]
 */
data class RandomizerCollection(
    val parameterRandomizers: Map<RDClassData, List<ParameterRandomizer<*>>>,
    val classRandomizers: Map<RDClassData, ClassRandomizer<*>>
) {

    @Inject
    constructor() : this(emptyMap(), emptyMap())

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
            classRandomizers = classRandomizers + newRandomizers.associateBy { it.returnedInstanceData }
        )
    }

    fun getRandomizer(key: RDClassData): ClassRandomizer<*>? {
        val k = classRandomizers.keys.firstOrNull { k ->
            k.kClass.isSubclassOf(key.kClass)
        }
        return classRandomizers[k]
    }

}
