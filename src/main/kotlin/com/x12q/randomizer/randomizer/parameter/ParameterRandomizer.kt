package com.x12q.randomizer.randomizer.parameter

import kotlin.reflect.KParameter
import com.github.michaelbull.result.Result
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.err.ErrorReport

/**
 * For randomizing parameters.
 * This is to specify random logic for parameters that meet certain requirement (name, type, parent class, etc).
 * For example:
 * - Generate float representing latitude and longitude.
 * - Generate string representing human name
 * - Generate string/number representing phone number
 */
interface ParameterRandomizer<out T> {
    val paramClassData: RDClassData

    /**
     * Check if this randomizer is applicable to a certain [parameter] or not.
     */
    fun isApplicableTo(
        parameterClassData: RDClassData,
        parameter: KParameter,
        parentClassData: RDClassData,
    ): Boolean

    fun random(
        parameterClassData: RDClassData,
        parameter: KParameter,
        parentClassData: RDClassData,
    ): T
}

