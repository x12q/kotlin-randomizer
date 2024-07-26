package com.x12q.randomizer.ir_plugin

import kotlin.reflect.KClass

class FactoryClassRandomizer<T : Any>(val makeRandom: () -> T, override val returnType: KClass<out T>) :
    ClassRandomizer<T> {
    override fun random(): T {
        return makeRandom()
    }
}
