package com.x12q.randomizer.randomizer

import com.x12q.randomizer.RDClassData

/**
 * Can generate a random instance of some type [T].
 */
interface ClassRandomizer<out T> : CommonRandomizer<T> {
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
