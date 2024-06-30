package com.x12q.randomizer

import kotlin.random.Random

interface RandomConfig{
    var random:Random
    var collectionSizeRange:IntRange
    fun nextString():String
    fun nextInt():Int
}


