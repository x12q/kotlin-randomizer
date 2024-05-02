package com.x12q.randomizer.randomizer.clazz

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.RDClassData

/**
 * A [ClassRandomizer] with custom condition check
 */
class ConditionalClassRandomizer<T>(
    override val returnedInstanceData: RDClassData,
    val condition: (targetClass: RDClassData, returnedInstanceClass: RDClassData) -> Boolean,
    val makeRandomIfApplicable: () -> T,
) : ClassRandomizer<T> {

    override fun isApplicableTo(classData: RDClassData): Boolean {
        return condition(classData, returnedInstanceData)
    }

    override fun random(): T {
        return makeRandomIfApplicable()
    }
}
