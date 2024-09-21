package com.x12q.randomizer.lib.util

fun <K,V> makeMap(size:Int, makePair:()->Pair<K,V>):Map<K,V>{
    return buildMap {
        val pair = makePair()
        put(pair.first, pair.second)
    }
}

fun <K,V> makePair(key:K,value:V):Pair<K,V>{
    return key to value
}
