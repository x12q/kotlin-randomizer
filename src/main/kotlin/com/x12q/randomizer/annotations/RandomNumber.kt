package com.x12q.randomizer.annotations

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.primitive.*
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

/**
 * Can be used on numbers + collections of numbers
 */
@Target(VALUE_PARAMETER, PROPERTY)
@Retention(RUNTIME)
annotation class RandomNumber(
    val from: Double = 0.0,
    val to: Double = 100.0,
) {
    companion object {
        fun RandomNumber.makeIntRandomizer(): ClassRandomizer<Int> {
            return intRandomizer(from.toInt()..to.toInt())
        }

        fun RandomNumber.makeIntParamRandomizer(): ParameterRandomizer<Int> {
            return intParamRandomizer(from.toInt()..to.toInt())
        }

        fun RandomNumber.makeDoubleRandomizer(): ClassRandomizer<Double> {
            return doubleRandomizer(from, to)
        }

        fun RandomNumber.makeDoubleParamRandomizer(): ParameterRandomizer<Double> {
            return doubleParamRandomizer(from, to)
        }

        fun RandomNumber.makeFloatRandomizer(): ClassRandomizer<Float> {
            return floatRandomizer(from.toFloat(), to.toFloat())
        }

        fun RandomNumber.makeFloatParamRandomizer(): ParameterRandomizer<Float> {
            return floatParamRandomizer(from.toFloat(), to.toFloat())
        }
    }
}

annotation class RandomInt
annotation class RandomDouble
annotation class RandomFloat
