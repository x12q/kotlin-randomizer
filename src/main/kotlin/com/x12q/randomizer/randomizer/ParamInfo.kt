package com.x12q.randomizer.randomizer

import com.x12q.randomizer.lookup_node.RDClassData
import kotlin.reflect.KParameter

data class ParamInfo(
    val kParam: KParameter,
    val paramClassData: RDClassData,
    val enclosingClassData: RDClassData,
){
    val paramName:String? = kParam.name

    val paramKClass = paramClassData.kClass
    val paramKType = paramClassData.kType

    val enclosingKClass = enclosingClassData.kClass
    val enclosingKType = enclosingClassData.kType

    inline fun <reified T> enclosingClassIs():Boolean{
        return enclosingKClass == T::class
    }

    inline fun <reified T> paramIs():Boolean{
        return paramKClass == T::class
    }

}
