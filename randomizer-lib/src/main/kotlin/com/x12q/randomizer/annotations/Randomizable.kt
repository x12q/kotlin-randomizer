package com.x12q.randomizer.annotations

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.err.RandomizerErrors
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.CommonRandomizer
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * Annotation to provide custom randomizer to class, constructor
 */
@Target(CLASS, VALUE_PARAMETER, PROPERTY, CONSTRUCTOR)
@Retention(RUNTIME)
annotation class Randomizable(
    val randomizer: KClass<out CommonRandomizer<*>> = CommonRandomizer.__DefaultRandomizer::class,
    val randomConfig:KClass<out RandomConfig> = DefaultRandomConfig::class
) {
    companion object {

        /**
         * try to cast [randomizer] as [ClassRandomizer].
         * Return an error only when [randomizer] is a [ParameterRandomizer], otherwise return the cast obj, or a null if there's any other error.
         */
        fun Randomizable.getClassRandomizerOnlyRs(targetClass: KClass<*>): Result<KClass<out ClassRandomizer<*>>?, ErrorReport> {
            val randomizerClass: KClass<out CommonRandomizer<*>> = this.randomizer
            val rt = if (randomizerClass == CommonRandomizer.__DefaultRandomizer::class) {
                Ok(null)
            } else {
                if (randomizerClass.isSubclassOf(ClassRandomizer::class)) {
                    Ok(randomizerClass as KClass<out ClassRandomizer<*>>)
                } else if (randomizerClass.isSubclassOf(ParameterRandomizer::class)) {
                    Err(
                        RandomizerErrors.CantApplyParamRandomizerToClass.report(
                            randomizerClass as ParameterRandomizer<*>,
                            targetClass
                        )
                    )
                } else {
                    Ok(null)
                }
            }
            return rt
        }

        fun Randomizable.getClassRandomizerOrParamRandomizerRs(): Result<Pair<KClass<out ClassRandomizer<*>>?, KClass<out ParameterRandomizer<*>>?>,ErrorReport> {
            val randomizerClass: KClass<out CommonRandomizer<*>> = this.randomizer
            val rt = if (randomizerClass == CommonRandomizer.__DefaultRandomizer::class) {
                Ok(Pair(null, null))
            } else {
                if (randomizerClass.isSubclassOf(ClassRandomizer::class)) {
                    Ok(Pair(randomizerClass as KClass<out ClassRandomizer<*>>, null))
                } else if (randomizerClass.isSubclassOf(ParameterRandomizer::class)) {
                    Ok(Pair(null, randomizerClass as KClass<out ParameterRandomizer<*>>))
                } else {
                    Err(RandomizerErrors.IllegalRandomizer.report(randomizerClass))
                }
            }
            return rt
        }
    }
}
