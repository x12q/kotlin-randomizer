package com.x12q.kotlin.randomizer.lib.randomizer

import com.x12q.kotlin.randomizer.lib.TypeKey


/**
 * A class randomizer that returns a constant
 */
data class ConstantRandomizer<T>(val value: T, override val returnType: TypeKey) : ClassRandomizer<T> {
    override fun random(): T {
        return value
    }

    companion object{
        inline fun <reified T> of(t:T): ConstantRandomizer<T> {
            return ConstantRandomizer(t, TypeKey.of<T>())
        }
    }
}
