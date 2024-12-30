package com.x12q.kotlin.randomizer.lib.util

import com.x12q.kotlin.randomizer.lib.ForKotlinRandomizerGeneratedCodeOnly

/**
 * Note:
 * - This file contains adapter functions that make it safer & more reliable for the randomizer plugin to lookup different overloads of functions from the kotlin standard library.
 * - These functions should not be called by anything other than the generated code
 * - Functions in this file must not have more than 1 overload.
 * - If there's a need for overloading, use a unique new name instead. That way, the plugin can avoid perform filter by itself.
 */

@ForKotlinRandomizerGeneratedCodeOnly
inline fun <reified T> makeArray(list:List<T>):Array<T>{
    return list.toTypedArray()
}

/**
 * Adapter function for making LinkedHashSet. This is not meant to be used by anything other than generated code.
 */
@ForKotlinRandomizerGeneratedCodeOnly
fun <T> makeLinkedHashSet(list: List<T>): LinkedHashSet<T> {
    return LinkedHashSet(list)
}

/**
 * Adapter function for making HashSet. This is not meant to be used by anything other than generated code.
 */
@ForKotlinRandomizerGeneratedCodeOnly
fun <T> makeHashSet(list: List<T>): HashSet<T> {
    return HashSet(list)
}

/**
 * Adapter function for making LinkedHashMap. This is not meant to be used by anything other than generated code.
 */
@ForKotlinRandomizerGeneratedCodeOnly
fun <K,V> makeLinkedHashMap(
    map:Map<K,V>
):LinkedHashMap<K,V>{
    return LinkedHashMap(map)
}

/**
 * Adapter function for making HashMap. This is not meant to be used by anything other than generated code.
 */
@ForKotlinRandomizerGeneratedCodeOnly
fun <K, V> makeHashMap(
    map:Map<K,V>
): HashMap<K, V> {
    return HashMap(map)
}

/**
 * Adapter function for making List. This is not meant to be used by anything other than generated code.
 */
@ForKotlinRandomizerGeneratedCodeOnly
fun <T> makeList(size: Int, makeElement: (index: Int) -> T): List<T> {
    return List(size, makeElement)
}

/**
 * Adapter function for making Map. This is not meant to be used by anything other than generated code.
 */
@ForKotlinRandomizerGeneratedCodeOnly
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
 * Adapter function for making Pair. This is not meant to be used by anything other than generated code.
 */
@ForKotlinRandomizerGeneratedCodeOnly
fun <K, V> makePair(key: K, value: V): Pair<K, V> {
    return key to value
}

/**
 * Adapter function to convert a list to a set. This is not meant to be used by anything other than generated code
 */
@ForKotlinRandomizerGeneratedCodeOnly
fun <T> listToSet(list: List<T>): Set<T> {
    return list.toSet()
}

/**
 * Adapter function to construct an [ArrayList] from a list. This is not meant to be used by anything other than generated code
 */
@ForKotlinRandomizerGeneratedCodeOnly
fun <T> makeArrayList(list: List<T>): ArrayList<T> {
    return ArrayList(list)
}
