package com.x12q.randomizer.randomizer_checker

import com.x12q.randomizer.randomizer.ClassRandomizer
import kotlin.reflect.KClass

/**
 * Reasons for invalid class randomizer
 */
sealed class InvalidClassRandomizerReason {
    abstract val rmdClass: KClass<out ClassRandomizer<*>>

    /**
     * When the checked class is an abstract class
     */
    data class IsAbstract(
        override val rmdClass: KClass<out ClassRandomizer<*>>
    ) : InvalidClassRandomizerReason()

    /**
     * When the checked class is a [ClassRandomizer] but cannot generate random instance of [targetClass]
     */
    data class UnableToGenerateTargetType(
        override val rmdClass: KClass<out ClassRandomizer<*>>,
        val actualClass: KClass<*>?,
        val targetClass: KClass<*>,
    ) : InvalidClassRandomizerReason()

    /**
     * When the checked class is not of type [ClassRandomizer]
     */
    data class IllegalClass(
        override val rmdClass: KClass<out ClassRandomizer<*>>,
    ) : InvalidClassRandomizerReason()
}
