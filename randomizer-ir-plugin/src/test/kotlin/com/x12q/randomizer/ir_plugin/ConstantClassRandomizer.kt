package com.x12q.randomizer.ir_plugin

import kotlin.reflect.KClass

class ConstantClassRandomizer<T:Any>(val i: T) : ClassRandomizer<T> {
    override val returnType: KClass<out T> = i::class
    override fun random(): T {
        return i
    }
}
