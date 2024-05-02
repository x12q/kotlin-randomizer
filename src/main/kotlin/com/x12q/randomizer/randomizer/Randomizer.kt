package com.x12q.randomizer.randomizer

/**
 * A signal interface, serve no other purpose other than allowing both [ParameterRandomizer] and [ClassRandomizer] to be assignable to a common type
 */
sealed interface Randomizer<out T>{
    abstract class __DefaultRandomizer private constructor() : Randomizer<Any>
}
