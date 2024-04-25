package com.x12q.randomizer.randomizer.class_randomizer

import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.parameter.Randomizer

/**
 * Can generate a random instance of some type [T].
 */
interface ClassRandomizer<out T> : Randomizer<T> {
    val targetClassData: RDClassData
    fun isApplicable(classData: RDClassData): Boolean
    fun random(): T
}
