package com.x12q.randomizer.randomizer.param

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import kotlin.reflect.KParameter

/**
 * A [ParameterRandomizer] with custom condition check.
 */
class ConditionalParamRandomizer<T>(
    override val paramClassData: RDClassData,
    /**
     * Check if this randomizer is applicable to certain parameter. This function has access to:
     * - class data + param data of the parameter
     * - class data of the class containing such parameter
     */
    val condition: (target: ParamInfo) -> Boolean,
    /**
     * Generate a random instance. This function has access to:
     * - class data + param data of the parameter
     * - class data of the class containing such parameter
     */
    val makeRandom: (ParamInfo) -> T,
) : ParameterRandomizer<T> {

    override fun isApplicableTo(paramInfo: ParamInfo): Boolean {
        return condition(paramInfo)
    }

    override fun random(
        parameterClassData: RDClassData,
        parameter: KParameter,
        enclosingClassData: RDClassData
    ): T? {
        val paramInfo = ParamInfo(
            paramClass = paramClassData,
            kParam = parameter,
            parentClass = enclosingClassData
        )
        if (this.isApplicableTo(paramInfo)) {
            return makeRandom(paramInfo)
        } else {
            return null
        }
    }
}
