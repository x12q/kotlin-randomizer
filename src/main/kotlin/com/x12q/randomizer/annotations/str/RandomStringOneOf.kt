package com.x12q.randomizer.annotations.str

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.stringRandomizer

annotation class RandomStringOneOf(
    val values: Array<String>
){
    companion object{
        fun RandomStringOneOf.makeClassRandomizer():ClassRandomizer<String> {
            return stringRandomizer {
                values.random()
            }
        }
    }
}
