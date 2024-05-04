package com.x12q.randomizer.randomizer

import kotlin.reflect.KParameter

data class ParamInfo(
    val kParam: KParameter,
    val paramClassData: RDClassData,
    val parentClassData: RDClassData,
){
    val paramName:String? = kParam.name

    val paramKClass = paramClassData.kClass
    val paramKType = paramClassData.kType

    val parentKClass = parentClassData.kClass
    val parentKType = parentClassData.kType

    inline fun <reified T> parentIs():Boolean{
        return parentKClass == T::class
    }

    inline fun <reified T> paramIs():Boolean{
        return paramKClass == T::class
    }

}
