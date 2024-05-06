package com.x12q.randomizer.randomizer.param

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer

abstract class AbsSameClassParamRandomizer<T>:ParameterRandomizer<T>{
    override fun isApplicableTo(
        paramInfo:ParamInfo
    ): Boolean{
        return paramInfo.paramClassData == this.paramClassData
    }
}
