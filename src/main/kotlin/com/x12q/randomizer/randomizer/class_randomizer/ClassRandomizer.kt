package com.x12q.randomizer.randomizer.class_randomizer

import com.x12q.randomizer.randomizer.RDClassData

/**
 * Can generate a random instance of some type [T].
 */
interface ClassRandomizer<T>  {
    val paramClassData: RDClassData
    fun isApplicable(classData: RDClassData): Boolean
    fun random(): T
}
