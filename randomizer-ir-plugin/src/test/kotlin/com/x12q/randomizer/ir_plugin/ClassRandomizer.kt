package com.x12q.randomizer.ir_plugin

import kotlin.reflect.KClass

interface ClassRandomizer<T : Any> {
    fun random(): T
    val returnType: KClass<out T>
}
