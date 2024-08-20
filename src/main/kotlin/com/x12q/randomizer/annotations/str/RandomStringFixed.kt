package com.x12q.randomizer.annotations.str

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.doubleRandomizer
import com.x12q.randomizer.randomizer.primitive.stringRandomizer



annotation class RandomStringFixed(
    val value:String
){
    companion object{
        fun RandomStringFixed.makeClassRandomizer(): ClassRandomizer<String> {
            return stringRandomizer(value)
        }
    }
}
