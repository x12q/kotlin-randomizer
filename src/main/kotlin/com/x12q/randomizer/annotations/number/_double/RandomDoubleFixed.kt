package com.x12q.randomizer.annotations.number._double

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.doubleRandomizer
import com.x12q.randomizer.randomizer.primitive.floatRandomizer

annotation class RandomDoubleFixed(
    val value:Double
){
    companion object{
        fun RandomDoubleFixed.makeClassRandomizer(): ClassRandomizer<Double> {
            return doubleRandomizer(value)
        }
    }
}
