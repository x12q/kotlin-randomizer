package com.x12q.randomizer.lib.randomizer

import kotlin.reflect.KClass

/**
 * A randomizer of some class [T]
 */
interface ClassRandomizer<T : Any> {
    fun random(): T
    val returnType: KClass<out T>
}
