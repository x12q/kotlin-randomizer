package com.x12q.randomizer.randomizer.clazz

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.RDClassData

/**
 * A [ClassRandomizer] whose condition checking is check if the target class is the same as [returnedInstanceData]
 */
class SameClassRandomizer<T>(
    override val returnedInstanceData: RDClassData,
    val makeRandom: () -> T
) : ClassRandomizer<T> {

    override fun isApplicable(classData: RDClassData): Boolean {
        return classData == this.returnedInstanceData
    }
    override fun random(): T {
        return makeRandom()
    }
}
