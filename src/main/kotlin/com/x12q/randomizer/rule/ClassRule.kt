package com.x12q.randomizer.rule

import kotlin.reflect.KClass

interface ClassRule{
    val clazz: KClass<*>
    fun factoryFunction()
}
