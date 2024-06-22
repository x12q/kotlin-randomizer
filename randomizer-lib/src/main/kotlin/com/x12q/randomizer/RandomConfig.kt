package com.x12q.randomizer

import kotlin.random.Random

interface RandomConfig{
    var random:Random
}


object DefaultRandomConfig : RandomConfig {
    override var random:Random = Random
    fun nextInt():Int{
        return random.nextInt()
    }
}
