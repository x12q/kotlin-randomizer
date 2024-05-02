package com.x12q.randomizer.randomizer

/**
 * Can generate a random instance of some type [T].
 */
interface ClassRandomizer<out T> : Randomizer<T> {
    /**
     * [RDClassData] of the type returned by this randomizer
     */
    val returnedInstanceData: RDClassData

    /**
     * Check if this randomizer is applicable a certain class
     */
    fun isApplicableTo(classData: RDClassData): Boolean

    /**
     * Create a random instance
     */
    fun random(): T
}
