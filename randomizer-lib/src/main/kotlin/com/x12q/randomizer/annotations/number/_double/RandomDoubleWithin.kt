package com.x12q.randomizer.annotations.number._double

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.doubleRandomizer
import com.x12q.randomizer.randomizer.primitive.floatRandomizer


annotation class RandomDoubleWithin(
    val from: Double, val to: Double
) {
    companion object {
        fun RandomDoubleWithin.makeClassRandomizer(): ClassRandomizer<Double> {
            return doubleRandomizer(from, to)
        }
    }
}
