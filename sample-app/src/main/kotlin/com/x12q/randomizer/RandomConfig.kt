package com.x12q.randomizer

import com.x12q.randomizer.DefaultRandomConfig.random
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


fun nextInt():Int{
    return DefaultRandomConfig.nextInt()
}
