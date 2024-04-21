package com.x12q.randomizer.randomizer.parameter

import com.x12q.randomizer.randomizer.RDClassData
import kotlin.reflect.KParameter

data class ParamInfo(
    val paramClass: RDClassData,
    val kParam: KParameter,
    val parentClass: RDClassData,
)
