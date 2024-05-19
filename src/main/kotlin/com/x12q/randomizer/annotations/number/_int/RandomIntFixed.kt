package com.x12q.randomizer.annotations.number._int

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import com.x12q.randomizer.randomizer.param.paramRandomizer
import com.x12q.randomizer.randomizer.primitive.intParamRandomizer
import com.x12q.randomizer.randomizer.primitive.intRandomizer

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class RandomIntFixed(
    val value:Int
){
    companion object{
        fun RandomIntFixed.makeClassRandomizer():ClassRandomizer<Int>{
            return intRandomizer(value)
        }
    }
}
