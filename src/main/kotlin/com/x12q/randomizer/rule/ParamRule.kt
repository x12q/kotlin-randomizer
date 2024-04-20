package com.x12q.randomizer.rule

import kotlin.reflect.KClass

interface ParamRule{
    val paramName:String
    val paramType: KClass<*>
    val parentClass: KClass<*>?
    fun factoryFunction()
}
