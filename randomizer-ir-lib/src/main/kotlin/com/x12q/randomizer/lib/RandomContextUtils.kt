package com.x12q.randomizer.lib


inline fun <reified T:Any> RandomizerCollection2.getRandomizer(): ClassRandomizer<T>?{
    val key = TypeKey.of<T>()
    return randomizersMap[key] as? ClassRandomizer<T>
}

inline fun <reified T:Any> RandomContext.random(): T?{
    val randomizer = this.getRandomizer<T>()
    val resultFromRandomizer = randomizer?.random()
    return resultFromRandomizer
}
