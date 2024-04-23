package com.x12q.randomizer

import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

/**
 * Requirement:
 * - recursive function to traverse the constructor tree, and init object.
 * - for abstract parameter, add @Randomizable(ConcreteClass1::class, ConcreteClass2::class, AnotherRandomizableAbstractClass::class)
 * - For abstract class, add the same @Randomizable, the one in parameter override the one in the class.
 * - For concrete class, add @Randomizable(factoryFunction=...) or @Randomizable(randomizer = CustomRandomizer::class)
 *
 * The top level random function:
 * - should it accept some kind of master rule that override everything?
 *      - if so, what should those rule looks like?
 *          - each param rule contains:
 *              - a param name
 *              - type/ class of the param
 *              - a Parent class
 *              - a factory function
 *          - each class rule contains:
 *              - a class name
 *              - a factory function
 *     - Rule for primitive types (string, int, etc) must be very easy to set
 */



@Target(CLASS, VALUE_PARAMETER, TYPE_PARAMETER, CONSTRUCTOR)
@Retention(RUNTIME)
annotation class Randomizable(
    val classRandomizer: KClass<out ClassRandomizer<*>> = __DefaultRandomizerClass::class,
){
    companion object{
        abstract class __DefaultRandomizerClass private constructor(): ClassRandomizer<Any>
    }
}

//    val paramRandomizer: KClass<out ParameterRandomizer<*>> = ParameterRandomizer::class,
