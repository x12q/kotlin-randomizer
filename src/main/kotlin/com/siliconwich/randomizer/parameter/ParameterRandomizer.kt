package com.siliconwich.randomizer.parameter

import com.siliconwich.randomizer.ClassData
import com.siliconwich.randomizer.err.RandomizerError
import kotlin.reflect.KParameter
import com.github.michaelbull.result.Result
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
    val paramClassData: ClassData

    /**
     * Check if this randomizer is applicable to a certain [parameter] or not.
     */
    fun isApplicableTo(parameter: KParameter):Boolean

    /**
     * Generate a random value
     */
    fun randomRs(parameter: KParameter):Result<T,RandomizerError>
    fun random(parameter: KParameter):T
}

