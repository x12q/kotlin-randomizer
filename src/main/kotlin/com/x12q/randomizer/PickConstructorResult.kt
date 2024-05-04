package com.x12q.randomizer

import kotlin.reflect.KFunction

class PickConstructorResult(
    val constructor: KFunction<Any>,
    val randomizable: Randomizable?,
)
