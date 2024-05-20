package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.floatRandomizer

annotation class RandomLongitude(
    val from: Float = -180f, val to: Float = 180f
) {
    companion object {
        fun RandomLongitude.makeClassRandomizer(): ClassRandomizer<Float> {
            return floatRandomizer(from, to)
        }
    }
}
