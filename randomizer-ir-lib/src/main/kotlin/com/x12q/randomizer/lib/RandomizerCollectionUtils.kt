package com.x12q.randomizer.lib


inline fun <reified T:Any> RandomizerCollection.getRandomizer(): ClassRandomizer<T>?{
    return randomizersMap[T::class] as? ClassRandomizer<T>
}

inline fun <reified T:Any> RandomizerCollection.random(): T?{
    val randomizer = this.getRandomizer<T>()
    return randomizer?.random()
}
