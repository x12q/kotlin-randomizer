package com.x12q.randomizer.randomizer

/**
 * Can generate a random instance of some type [T].
 */
interface ClassRandomizer<out T> : Randomizer<T> {
    val targetClassData: RDClassData
    fun isApplicable(classData: RDClassData): Boolean
    fun random(): T
}
