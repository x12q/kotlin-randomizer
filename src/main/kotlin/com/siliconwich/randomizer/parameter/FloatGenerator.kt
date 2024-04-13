package com.siliconwich.randomizer.param

import com.siliconwich.randomizer.ClassData
import kotlin.random.Random
import kotlin.reflect.KParameter
import kotlin.reflect.typeOf

class FloatGenerator : ParameterRandomizer<Float> {
    override val key: ClassData
        get() = TODO("Not yet implemented")

    override fun isApplicableTo(parameter: KParameter): Boolean {
        return parameter.type == typeOf<Float>()
    }
    override fun random(parameter: KParameter): Float {
        if(this.isApplicableTo(parameter)){
            return Random.nextFloat()
        }else{
            throw IllegalArgumentException()
        }
    }
}
