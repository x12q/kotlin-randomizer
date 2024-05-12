package com.x12q.randomizer.randomizer

import com.x12q.randomizer.RDClassData
import javax.inject.Inject
import kotlin.random.Random
import kotlin.reflect.full.isSubclassOf

/**
 * A collection of [ClassRandomizer] and [ParameterRandomizer].
 * For one class, there can be multiple randomizers. But only one will be used to generate such class.
 * Such selection is random for [classRandomizers], for [parameterRandomizers] condition check is performed.
 */
data class RandomizerCollection(
    val parameterRandomizers: Map<RDClassData, List<ParameterRandomizer<*>>>,
    val classRandomizers: Map<RDClassData, List<ClassRandomizer<*>>>,
    val random: Random = Random,
) {

    @Inject
    constructor(
        random: Random
    ) : this(emptyMap(), emptyMap(),random)

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
        val newMap:MutableMap<RDClassData, List<ClassRandomizer<*>>> = classRandomizers.toMutableMap()
        val newRandomizersMap = newRandomizers.groupBy { it.returnedInstanceData }

        for(newRdm in newRandomizersMap){
            val lst = newMap[newRdm.key]
            val newLst = if(lst!=null){
                lst + newRdm.value
            }else{
                newRdm.value
            }
            newMap[newRdm.key] = newLst
        }

        return this.copy(
            classRandomizers = newMap.toMap()
        )
    }

    fun getRandomizer(key: RDClassData): ClassRandomizer<*>? {
        val rt = this.classRandomizers.filter { it.key.kClass.isSubclassOf(key.kClass) }.values.flatten().randomOrNull()
        return rt
    }

}
