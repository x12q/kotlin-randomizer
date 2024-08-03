package com.x12q.randomizer.lib

import kotlin.reflect.KClass

/**
 * A class randomizer that returns a constant
 */
class ConstantClassRandomizer<T:Any>(val value: T,override val returnType: KClass<T>) : ClassRandomizer<T> {
    override fun random(): T {
        return value
    }
}
