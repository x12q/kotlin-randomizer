package com.siliconwich.randomizer.parameter

import com.github.michaelbull.result.*
import com.siliconwich.randomizer.ClassData
import com.siliconwich.randomizer.Randomizer
import com.siliconwich.randomizer.err.RandomizerError
import kotlin.random.Random
import kotlin.reflect.KParameter
import kotlin.reflect.typeOf

data class FloatRandomizer(val random: Random) : ParameterRandomizer<Float> {

    override val paramClassData: ClassData = ClassData.from<Float>()

    override fun isApplicableTo(parameter: KParameter): Boolean {
        return parameter.type == typeOf<Float>()
    }

    override fun randomRs(parameter: KParameter): Result<Float, RandomizerError> {
        if (this.isApplicableTo(parameter)) {
            return Ok(random.nextFloat())
        } else {
            return Err(RandomizerError.CantApplyRandomizer)
        }
    }

    override fun random(parameter: KParameter): Float {
        when (val rt = randomRs(parameter)) {
            is Ok -> return rt.value
            is Err -> throw rt.error
        }
    }
}
