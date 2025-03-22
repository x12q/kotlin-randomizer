package com.x12q.kotlin.randomizer.lib

import kotlin.reflect.KProperty

data class PropertyKey(
    val propertyKClass: KProperty<*>,
    val parentKClass: TypeKey,
)
