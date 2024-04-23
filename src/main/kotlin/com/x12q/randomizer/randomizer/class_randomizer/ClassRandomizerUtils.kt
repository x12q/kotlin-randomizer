package com.x12q.randomizer.randomizer.class_randomizer

import com.x12q.randomizer.randomizer.RDClassData


inline fun <reified Q, reified T>diffType():Boolean{
    return RDClassData.from<Q>() != RDClassData.from<T>()
}

/**
 * Create a [ClassRandomizer] that perform checking using [condition] and generate random instances using [makeRandomIfApplicable]
 */
inline fun <reified T> randomizer(
    crossinline condition: (RDClassData) -> Boolean,
    crossinline makeRandomIfApplicable: () -> T,
): ClassRandomizer<T> {
    val rt = object : ClassRandomizer<T> {
        override val targetClassData: RDClassData = RDClassData.from<T>()
        override fun isApplicable(classData: RDClassData): Boolean {
            return condition(classData)
        }

        override fun random(): T {
            return makeRandomIfApplicable()
        }
    }
    return rt
}

