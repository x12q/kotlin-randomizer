package com.x12q.randomizer.lib

import kotlin.reflect.KClass

/**
 * A class randomizer that run a factory function to produce random values
 */
class FactoryClassRandomizer<T : Any>(
    val makeRandom: () -> T,
    override val returnType: KClass<T>
) :
    ClassRandomizer<T> {
    override fun random(): T {
        return makeRandom()
    }
}
