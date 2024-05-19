package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.floatRandomizer

annotation class RandomFloatOneOf(
    val values: FloatArray
){
    companion object{
        fun RandomFloatOneOf.makeClassRandomizer():ClassRandomizer<Float> {
            return floatRandomizer {
                values.random()
            }
        }
    }
}
