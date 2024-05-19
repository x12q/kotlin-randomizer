package com.x12q.randomizer.randomizer

import com.x12q.randomizer.RDClassData
import kotlin.reflect.KParameter

/**
 * For randomizing parameters.
 * This is to specify random logic for parameters that meet certain requirement (name, type, parent class, etc).
 * For example:
 *  - Generate float representing latitude and longitude.
 *  - Generate string representing human name
 *  - Generate string/number representing phone number
 */
interface ParameterRandomizer<out T> : CommonRandomizer<T> {
    /**
     * class data of the param that can be generated by this randomizer
     */
    val paramClassData: RDClassData

    /**
     * Check if this randomizer is applicable to a certain [parameter] or not.
     */
    fun isApplicableTo(
        paramInfo:ParamInfo
    ): Boolean

    /**
     * return a new instance if condition check pass, a null otherwise
     */
    fun random(
        parameterClassData: RDClassData,
        parameter: KParameter,
        enclosingClassData: RDClassData,
    ):T?
}

