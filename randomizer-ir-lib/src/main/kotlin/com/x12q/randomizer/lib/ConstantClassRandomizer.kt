package com.x12q.randomizer.lib

import kotlin.reflect.KClass

/**
 * A class randomizer that returns a constant
 */
data class ConstantClassRandomizer<T:Any>(val value: T,override val returnType: KClass<T>) : ClassRandomizer<T> {
    override fun random(): T {
        return value
    }

    companion object{
        inline fun <reified T:Any> of(t:T): ConstantClassRandomizer<T> {
            return ConstantClassRandomizer(t,T::class)
        }
    }
}
