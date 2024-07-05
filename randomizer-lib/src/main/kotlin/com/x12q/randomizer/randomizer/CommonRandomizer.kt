package com.x12q.randomizer.randomizer

import com.x12q.randomizer.annotations.Randomizable

/**
 * A common-ground interface, serve no other purpose other than allowing both [ParameterRandomizer] and [ClassRandomizer] to be assignable to a common type in [Randomizable]
 */
sealed interface CommonRandomizer<out T>{
    /**
     * this implementation only serve the purpose of being a flag-like argument.
     */
    abstract class __DefaultRandomizer private constructor() : CommonRandomizer<Any>
}


