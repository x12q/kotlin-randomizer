package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.floatRandomizer

annotation class RandomFloatWithin(
    val from: Float, val to: Float
) {
    companion object {
        fun RandomFloatWithin.makeClassRandomizer(): ClassRandomizer<Float> {
            return floatRandomizer(from, to)
        }
    }
}
