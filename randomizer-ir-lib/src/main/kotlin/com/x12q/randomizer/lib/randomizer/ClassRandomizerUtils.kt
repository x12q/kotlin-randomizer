package com.x12q.randomizer.lib.randomizer

inline fun <reified T:Any> List<ClassRandomizer<*>>.getRandomizer(): ClassRandomizer<T>? {
    val rt = this.firstOrNull {
        it.returnType == T::class
    }
    return rt?.let { it as? ClassRandomizer<T> }
}

inline fun <reified T:Any> ClassRandomizerCollection.getRandomizer(): ClassRandomizer<T>?{
    return this.randomizers.getRandomizer<T>()
}

inline fun <reified T:Any> ClassRandomizerCollection.random(): T?{
    val randomizer = this.getRandomizer<T>()
    return randomizer?.random()
}
