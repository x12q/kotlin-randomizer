package com.x12q.randomizer

import com.x12q.randomizer.randomizer.ClassRandomizer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

data class PickConstructorResult(
    val constructor: KFunction<Any>,
    val randomizer: KClass<out ClassRandomizer<*>>?
)
