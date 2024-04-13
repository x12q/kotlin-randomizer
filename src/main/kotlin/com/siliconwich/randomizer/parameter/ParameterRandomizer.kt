package com.siliconwich.randomizer.param

import com.siliconwich.randomizer.ClassData
import kotlin.reflect.KParameter

/**
 *
 */
interface ParameterRandomizer<out T> {
    val key: ClassData
    fun isApplicableTo(parameter: KParameter):Boolean
    fun random(parameter: KParameter):T
}

