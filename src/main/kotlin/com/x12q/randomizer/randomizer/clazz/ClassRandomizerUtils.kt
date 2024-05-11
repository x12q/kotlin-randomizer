package com.x12q.randomizer.randomizer.clazz

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.RDClassData

/**
 * Create a [SameClassRandomizer]
 */
inline fun <reified T> classRandomizer(
    crossinline random:()->T
): ClassRandomizer<T> {
    return SameClassRandomizer(
        returnedInstanceData = RDClassData.from<T>(),
        makeRandom = {
            random()
        },
    )
}

/**
 * create a [ConditionalClassRandomizer]
 */
inline fun <reified T> classRandomizer(
    crossinline condition: (targetClass: RDClassData, returnedInstanceClass: RDClassData) -> Boolean,
    crossinline random: () -> T,
): ClassRandomizer<T> {
    return ConditionalClassRandomizer(
        returnedInstanceData = RDClassData.from<T>(),
        condition = {targetClass,returnedInstanceClass->
            condition(targetClass,returnedInstanceClass)
        },
        makeRandomIfApplicable={
            random()
        }
    )
}
