package com.x12q.randomizer.randomizer.chain

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer

/**
 * This function is for chaining a series of factory functions into a randomizer.
 */
inline fun <T, reified E> ClassRandomizer<T>.then(crossinline nextRandomizer: (T) -> E): ClassRandomizer<E> {
    val rt = classRandomizer {
        val prevRdValue = this.random()
        nextRandomizer(prevRdValue)
    }
    return rt
}
