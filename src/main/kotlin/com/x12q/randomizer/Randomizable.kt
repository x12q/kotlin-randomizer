package com.x12q.randomizer

import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

/**
 *
 */
@Target(CLASS, VALUE_PARAMETER, TYPE_PARAMETER, CONSTRUCTOR)
@Retention(RUNTIME)
annotation class Randomizable(
    val classRandomizer: KClass<out ClassRandomizer<*>> = __DefaultClassRandomizer::class,
    val paramRandomizer: KClass<out ParameterRandomizer<*>> = __DefaultParamRandomizer::class,
){
    companion object{
        abstract class __DefaultClassRandomizer private constructor(): ClassRandomizer<Any>
        abstract class __DefaultParamRandomizer private constructor(): ParameterRandomizer<Any>
    }
}
