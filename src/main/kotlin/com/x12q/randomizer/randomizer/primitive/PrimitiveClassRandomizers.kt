package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer


/**
 * Convenient function to create a [ClassRandomizer] that can produce random integers
 */
fun intRandomizer(
    random: () -> Int
): ClassRandomizer<Int> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random floats
 */
fun floatRandomizer(
    random: () -> Float
): ClassRandomizer<Float> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random strings
 */
fun stringRandomizer(
    random: () -> String
): ClassRandomizer<String> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random doubles
 */
fun doubleRandomizer(
    random: () -> Double
): ClassRandomizer<Double> {
    return classRandomizer(random)
}


/**
 * Convenient function to create a [ClassRandomizer] that can produce random bytes
 */
fun byteRandomizer(
    random: () -> Byte
): ClassRandomizer<Byte> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random shorts
 */
fun shortRandomizer(
    random: () -> Short
): ClassRandomizer<Short> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random booleans
 */
fun booleanRandomizer(
    random: () -> Boolean
): ClassRandomizer<Boolean> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random long
 */
fun longRandomizer(
    random: () -> Long
): ClassRandomizer<Long> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random characters
 */
fun charRandomizer(
    random: () -> Char
): ClassRandomizer<Char> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce a random list
 */
fun <T> listRandomizer(
    random:()->List<T>
):ClassRandomizer<List<T>>{
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce a random map
 */
fun <K,V> mapRandomizer(
    random: ()->Map<K,V>
):ClassRandomizer<Map<K,V>>{
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce a random set
 */
fun <T> setRandomizer(
    random: ()->Set<T>
):ClassRandomizer<Set<T>>{
    return classRandomizer(random)
}

