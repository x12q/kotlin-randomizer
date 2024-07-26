package com.x12q.randomizer.ir_plugin

inline fun <reified T:Any> List<ClassRandomizer<*>>.getRandomizer(): ClassRandomizer<T>? {
    val rt = this.firstOrNull {
        it.returnType == T::class
    }
    return rt?.let { it as? ClassRandomizer<T> }
}

inline fun <reified T:Any> ClassRandomizerCollection.getRandomizer():ClassRandomizer<T>?{
    return this.randomizers.getRandomizer<T>()
}
