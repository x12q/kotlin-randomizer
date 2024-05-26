package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.floatRandomizer

annotation class RandomFloatFixed(
    val value:Float
){
    companion object{
        fun RandomFloatFixed.makeClassRandomizer(): ClassRandomizer<Float> {
            return floatRandomizer(value)
        }
    }
}
