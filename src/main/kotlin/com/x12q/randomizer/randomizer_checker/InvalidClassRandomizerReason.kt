package com.x12q.randomizer.randomizer_checker

import com.x12q.randomizer.randomizer.ClassRandomizer
import kotlin.reflect.KClass

/**
 * Reasons for invalid class randomizer
 */
sealed class InvalidClassRandomizerReason(msg:String) : Exception(msg) {

    /**
     * When the checked class is an abstract class
     */
    data class IsAbstract(
        val rmdClass: KClass<out ClassRandomizer<*>>
    ) : InvalidClassRandomizerReason(
        "[${rmdClass}] is abstract. Can't use an abstract class randomizer"
    )

    /**
     * When the checked class is a [ClassRandomizer] but cannot generate random instance of [targetClass]
     */
    data class UnableToGenerateTargetType(
        val rmdClass: KClass<out ClassRandomizer<*>>,
        val actualClass: KClass<*>?,
        val targetClass: KClass<*>,
    ) : InvalidClassRandomizerReason(
        "[${rmdClass}] cannot generate instances for [${actualClass}]"
    )

    /**
     * When the checked class is not of type [ClassRandomizer]
     */
    data class IllegalClass(
        val clazz: KClass<*>,
    ) : InvalidClassRandomizerReason(
        "[${clazz}] is not a class randomizer"
    )
}
