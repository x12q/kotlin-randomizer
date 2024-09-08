package com.x12q.randomizer.lib


/**
 * A class randomizer that returns a constant
 */
data class ConstantClassRandomizer<T:Any>(val value: T,override val returnType: TypeKey) : ClassRandomizer<T> {
    override fun random(): T {
        return value
    }

    companion object{
        inline fun <reified T:Any> of(t:T): ConstantClassRandomizer<T> {
            return ConstantClassRandomizer(t,TypeKey.of<T>())
        }
    }
}
