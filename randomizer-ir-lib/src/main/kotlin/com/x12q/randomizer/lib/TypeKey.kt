package com.x12q.randomizer.lib

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class TypeKey(
    val kClass: KClass<*>,
    val kType: KType?,
) {
    companion object {
        inline fun <reified T> of(): TypeKey {
            val c = T::class
            val t = typeOf<T>()
            return TypeKey(
                kClass = c,
                kType = t,
            )
        }
    }
}
