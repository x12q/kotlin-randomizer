package com.x12q.randomizer.annotations.number._int

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.primitive.intParamRandomizer
import com.x12q.randomizer.randomizer.primitive.intRandomizer

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class RandomIntWithin(
    val from: Int = 0,
    val to: Int = 100,
){
    companion object{
        fun RandomIntWithin.makeClassRandomizer(): ClassRandomizer<Int> {
            return intRandomizer(from..to)
        }
    }
}
