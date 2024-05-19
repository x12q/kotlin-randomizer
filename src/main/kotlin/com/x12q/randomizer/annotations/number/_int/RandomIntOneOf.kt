package com.x12q.randomizer.annotations.number._int

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import com.x12q.randomizer.randomizer.param.paramRandomizer

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class RandomIntOneOf(
    val values:IntArray
){
    companion object{
        fun RandomIntOneOf.makeClassRandomizer():ClassRandomizer<Int>{
            return classRandomizer {
                values.random()
            }
        }
        fun RandomIntOneOf.makeParamRandomizer(): ParameterRandomizer<Int> {
            return paramRandomizer {
                values.random()
            }
        }
    }
}
