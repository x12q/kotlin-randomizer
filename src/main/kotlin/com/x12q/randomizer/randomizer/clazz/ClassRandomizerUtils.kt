package com.x12q.randomizer.randomizer.clazz

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.RandomContext
import com.x12q.randomizer.random
import com.x12q.randomizer.randomFromContext

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

inline fun <reified T> RandomContext.classRandomizer(): ClassRandomizer<T> {
    val context = this
    return SameClassRandomizer(
        returnedInstanceData = RDClassData.from<T>(),
        makeRandom = {
            randomFromContext<T>(context)
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
