package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer
import kotlin.reflect.typeOf


inline fun <reified T:Any> RandomizerCollection.getRandomizer(): ClassRandomizer<T>?{
    val key = TypeKey.of<T>()
    return randomizersMap[key] as? ClassRandomizer<T>
}

inline fun <reified T:Any> RandomContext.random(): T?{
    val randomizer = this.getRandomizer<T>()
    val resultFromRandomizer = randomizer?.random()
    return resultFromRandomizer
}

inline fun <reified T:Any> RandomContext.randomOrThrow(): T{
    val randomizer = this.getRandomizer<T>()
    if(randomizer!=null){
        val resultFromRandomizer = randomizer.random()
        return resultFromRandomizer
    }else{
        throw UnableToMakeRandomException("Unable to make random of type ${typeOf<T>()}")
    }
}
