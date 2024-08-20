package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.floatRandomizer


annotation class RandomLatitude(
    val from: Float = -90f, val to: Float = 90f
) {
    companion object {
        fun RandomLatitude.makeClassRandomizer(): ClassRandomizer<Float> {
            return floatRandomizer(from, to)
        }
    }
}
