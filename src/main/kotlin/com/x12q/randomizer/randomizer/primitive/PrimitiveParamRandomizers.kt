package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.param.paramRandomizer


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random integers
 */
fun intParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Int,
): ParameterRandomizer<Int> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random floats
 */
fun floatParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Float,
): ParameterRandomizer<Float> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random strings
 */
fun stringParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> String,
): ParameterRandomizer<String> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random doubles
 */
fun doubleParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Double,
): ParameterRandomizer<Double> {
    return paramRandomizer(condition, random)
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random bytes
 */
fun byteParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Byte,
): ParameterRandomizer<Byte> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random shorts
 */
fun shortParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Short,
): ParameterRandomizer<Short> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random booleans
 */
fun booleanParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Boolean,
): ParameterRandomizer<Boolean> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random long
 */
fun longParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Long,
): ParameterRandomizer<Long> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random characters
 */
fun charParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Char,
): ParameterRandomizer<Char> {
    return paramRandomizer(condition, random)
}

