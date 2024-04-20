package com.x12q.randomizer.randomizer.parameter

import kotlin.reflect.KParameter
import com.github.michaelbull.result.Result
import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.err.ErrorReport

/**
 * For randomizing parameter.
 * This can override the default randomizer to generate special random value for paramters that meet its requirement.
 * For example:
 * - Generate randomized latitude and longitude.
 * - Generate random human name
 * - Generate random phone number
 * - etc
 */
interface ParameterRandomizer<out T> {
    val paramClassData: RDClassData

    /**
     * Check if this randomizer is applicable to a certain [parameter] or not.
     */
    fun isApplicableTo(parameterClassData: RDClassData, parameter: KParameter): Boolean

    /**
     * Generate a random value
     */
    fun randomRs(parameterClassData: RDClassData, parameter: KParameter): Result<T, ErrorReport>
    fun random(parameterClassData: RDClassData, parameter: KParameter): T?
}

