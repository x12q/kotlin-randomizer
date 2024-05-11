package com.x12q.randomizer.randomizer.param

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.RDClassData

/**
 * Create a [ParameterRandomizer] that check parameter with [condition], and generate random instances with [random]
 */
inline fun <reified T> paramRandomizer(
    crossinline condition: (target: ParamInfo) -> Boolean,
    crossinline random: (ParamInfo) -> T,
): ParameterRandomizer<T> {
    return ConditionalParamRandomizer(
        paramClassData = RDClassData.from<T>(),
        condition = { target ->
            condition(target)
        },
        makeRandom = { paramInfo ->
            random(paramInfo)
        }
    )
}

/**
 * Create a [ParameterRandomizer] that only check for type match
 */
inline fun <reified T> paramRandomizer(
    crossinline random: (ParamInfo) -> T,
): ParameterRandomizer<T> {
    return SameClassParamRandomizer(
        paramClassData = RDClassData.from<T>(),
        random = { paramInfo ->
            random(paramInfo)
        }
    )
}
