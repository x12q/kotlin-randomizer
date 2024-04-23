package com.x12q.randomizer.annotation_processor.param

import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

sealed class InvalidParamRandomizerReason {
    abstract val randomizerClass: KClass<out ParameterRandomizer<*>>
    abstract val parentClass: KClass<*>
    abstract val targetParam: KParameter

    /**
     * When the checked class is an abstract class
     */
    data class IsAbstract(
        override val randomizerClass: KClass<out ParameterRandomizer<*>>,
        override val targetParam: KParameter,
        override val parentClass: KClass<*>
    ) : InvalidParamRandomizerReason()

    /**
     * When the checked class is a [ClassRandomizer] but cannot generate random instance of [targetClass]
     */
    data class UnableToGenerateTarget(
        override val randomizerClass: KClass<out ParameterRandomizer<*>>,
        override val targetParam: KParameter,
        override val parentClass: KClass<*>,
        val actualClass: KClass<*>?,
        val targetClass: KClass<*>,
    ) : InvalidParamRandomizerReason()

    /**
     * When the checked class is not of type [ClassRandomizer]
     */
    data class IllegalClass(
        override val randomizerClass: KClass<out ParameterRandomizer<*>>,
        override val targetParam: KParameter,
        override val parentClass: KClass<*>,
    ) : InvalidParamRandomizerReason()

    data class InvalidTarget(
        override val randomizerClass: KClass<out ParameterRandomizer<*>>,
        override val parentClass: KClass<*>,
        override val targetParam: KParameter
    ):InvalidParamRandomizerReason()
}
