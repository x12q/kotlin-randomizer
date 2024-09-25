package com.x12q.randomizer.lib.util

import com.x12q.randomizer.lib.ForGeneratedCodeOnly

/**
 * Guide:
 * - This file contains adapter functions that make it safer & more reliable for the randomizer plugin to lookup different overloads of functions from the kotlin standard library.
 * - However, these functions should not be called by anything other than the generated code
 * - Functions in this file must not have more than 1 overload.
 * - If there's a need for overloading, use a unique new name instead. That way, the plugin can avoid perform filter by itself.
 */


/**
 * Adapter function for making List. This is not meant to be used by anything other than the randomizer plugin.
 */
@ForGeneratedCodeOnly
fun <T> makeList(size:Int, makeElement:(index:Int)->T):List<T>{
    return List(size,makeElement)
}

/**
 * Adapter function for making Map. This is not meant to be used by anything other than the randomizer plugin.
 */
@ForGeneratedCodeOnly
fun <K, V> makeMap(
    size: Int,
    makeKey: () -> K,
    makeValue: () -> V,
): Map<K, V> {
    return buildMap {
        repeat(size) {
            put(makeKey(), makeValue())
        }
    }
}

/**
 * Adapter function for making Pair. This is not meant to be used by anything other than the randomizer plugin.
 */
@ForGeneratedCodeOnly
fun <K, V> makePair(key: K, value: V): Pair<K, V> {
    return key to value
}

/**
 * Adapter function to convert a list to a set. This is not meant to be used by anything other than the randomizer plugin.
 */
@ForGeneratedCodeOnly
fun <T> listToSet(list:List<T>):Set<T>{
    return list.toSet()
}
