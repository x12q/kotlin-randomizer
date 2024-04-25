package com.x12q.randomizer.randomizer.parameter

import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer

/**
 * A signal interface, serve no other purpose other than allowing both [ParameterRandomizer] and [ClassRandomizer] to be assignable to a common type
 */
interface Randomizer<out T>
