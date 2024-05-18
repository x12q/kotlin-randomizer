package com.x12q.randomizer.randomizer

import com.x12q.randomizer.annotations.Randomizable

/**
 * A signal interface, serve no other purpose other than allowing both [ParameterRandomizer] and [ClassRandomizer] to be assignable to a common type in [Randomizable]
 */
sealed interface Randomizer<out T>{
    abstract class __DefaultRandomizer private constructor() : Randomizer<Any>
}


