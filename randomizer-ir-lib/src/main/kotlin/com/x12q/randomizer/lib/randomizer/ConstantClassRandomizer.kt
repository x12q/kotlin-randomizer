package com.x12q.randomizer.lib.randomizer

import kotlin.reflect.KClass

/**
 * A class randomizer that returns a constant
 */
class ConstantClassRandomizer<T:Any>(val value: T) : ClassRandomizer<T> {
    override val returnType: KClass<out T> = value::class
    override fun random(): T {
        return value
    }
}
