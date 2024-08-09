package com.x12q.randomizer.lib

import kotlin.random.Random


inline fun <reified T:Any> RandomizerCollection.getRandomizer(): ClassRandomizer<T>?{
    return randomizersMap[T::class] as? ClassRandomizer<T>
}

inline fun <reified T:Any> RandomizerCollection.random(): T?{
    println(T::class)
    val randomizer = this.getRandomizer<T>()
    return randomizer?.random()
}

inline fun <reified T:Any> RandomizerCollection.randomOrNull(random:Random): T?{
    val randomizer = this.getRandomizer<T>()
    val v1 = randomizer?.random()
    return if(random.nextBoolean()) v1 else null
}
