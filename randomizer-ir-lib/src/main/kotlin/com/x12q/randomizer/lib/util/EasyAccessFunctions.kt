package com.x12q.randomizer.lib.util

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

fun <K, V> makePair(key: K, value: V): Pair<K, V> {
    return key to value
}
