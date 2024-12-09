package com.x12q.randomizer.lib.randomizer

import com.x12q.randomizer.lib.TypeKey


/**
 * A class randomizer that returns a constant
 */
data class ConstantRandomizer<T:Any>(val value: T, override val returnType: TypeKey) : ClassRandomizer<T> {
    override fun random(): T {
        return value
    }

    companion object{
        inline fun <reified T:Any> of(t:T): ConstantRandomizer<T> {
            return ConstantRandomizer(t, TypeKey.of<T>())
        }
    }
}
