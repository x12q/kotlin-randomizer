package com.x12q.randomizer.randomizer.primitive

import kotlin.random.Random

interface IntRandomizer {
    fun random():Int
}

class IntRandomizerBuilder(
    val random: Random
) {

}

object IntRandomizers {
//    fun from1to10():IntRandomizerBuilder{
//
//    }
}
