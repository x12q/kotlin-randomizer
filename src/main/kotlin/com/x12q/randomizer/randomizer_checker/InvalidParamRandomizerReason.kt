package com.x12q.randomizer.randomizer_checker

import com.x12q.randomizer.randomizer.ParameterRandomizer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

sealed class InvalidParamRandomizerReason(msg: String) : Exception(msg) {

    /**
     * When the checked class is an abstract class
     */
    data class IsAbstract(
        val randomizerClass: KClass<out ParameterRandomizer<*>>,
    ) : InvalidParamRandomizerReason(
        "[${randomizerClass}] is abstract. Can't use an abstract class randomizer"
    )

    /**
     * When the checked class is a [ClassRandomizer] but cannot generate random instance of [targetClass]
     */
    data class UnableToGenerateTarget(
        val randomizerClass: KClass<out ParameterRandomizer<*>>,
        val actualClass: KClass<*>?,
    ) : InvalidParamRandomizerReason(
        "[${randomizerClass}] cannot generate instances for [${actualClass}]"
    )

    /**
     * When the checked class is not of type [ClassRandomizer]
     */
    data class IllegalRandomizerClass(
        val randomizerClass: KClass<*>,
        val targetParam: KParameter,
        val parentClass: KClass<*>,
    ) : InvalidParamRandomizerReason(
        "[${randomizerClass}] is not a param randomizer"
    )

    data object InvalidTarget : InvalidParamRandomizerReason("Invalid param random target. Can only generate random for KClass or KParameter")
}
