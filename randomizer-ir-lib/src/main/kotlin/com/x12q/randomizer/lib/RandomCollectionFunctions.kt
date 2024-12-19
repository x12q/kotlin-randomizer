package com.x12q.randomizer.lib

import kotlin.jvm.Throws

/**
 * Construct a random [Map] using a [RandomContext]
 */
@Deprecated("not relevant to generated code")
@Throws(UnableToMakeRandomException::class)
inline fun <reified K:Any,reified V:Any> RandomContext.randomMap():Map<K,V>{
    val randomContext = this
    val mapSize = randomContext.randomCollectionSize()
    val rt = buildMap {
        repeat(mapSize){
            val key = randomContext.randomOrThrow<K>()
            val value = randomContext.randomOrThrow<V>()
            put(key,value)
        }
    }
    return rt
}


/**
 * Construct a random [List] using a [RandomContext]
 */
@Deprecated("not relevant to generated code")
@Throws(UnableToMakeRandomException::class)
inline fun <reified T:Any> RandomContext.randomList():List<T>{
    val randomContext = this
    val mapSize = randomContext.randomCollectionSize()
    if(mapSize==0){
        return emptyList()
    }else{
        val rt = List(mapSize){
            randomContext.randomOrThrow<T>()
        }
        return rt
    }
}


