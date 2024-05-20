package com.x12q.randomizer.annotations.number._double

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.doubleRandomizer
import com.x12q.randomizer.randomizer.primitive.floatRandomizer

annotation class RandomDoubleOneOf(
    val values: DoubleArray
){
    companion object{
        fun RandomDoubleOneOf.makeClassRandomizer():ClassRandomizer<Double> {
            return doubleRandomizer {
                values.random()
            }
        }
    }
}
