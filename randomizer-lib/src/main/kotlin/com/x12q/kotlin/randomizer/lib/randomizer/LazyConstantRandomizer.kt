package com.x12q.kotlin.randomizer.lib.randomizer

import com.x12q.kotlin.randomizer.lib.TypeKey

data class LazyConstantRandomizer<T:Any>(val makeValue:()->T,override val returnType: TypeKey) : ClassRandomizer<T> {

    private val value: T by lazy {
        makeValue()
    }
    override fun random(): T {
        return value
    }

    companion object{
        inline fun <reified T:Any> of(noinline makeValue:()->T): LazyConstantRandomizer<T> {
            return LazyConstantRandomizer(makeValue, TypeKey.of<T>())
        }
    }
}

