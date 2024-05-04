package com.x12q.randomizer.randomizer.param

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.RDClassData

class SameClassParamRandomizer<T> private constructor(
    private val conditionalParamRandomizer: ConditionalParamRandomizer<T>
) : ParameterRandomizer<T> by conditionalParamRandomizer {

    constructor(
        paramClassData: RDClassData,
        random: (ParamInfo) -> T,
    ) : this(
        ConditionalParamRandomizer<T>(
            paramClassData = paramClassData,
            condition = { target: ParamInfo ->
                target.paramClassData == paramClassData
            },
            makeRandom = random
        )
    )
}
