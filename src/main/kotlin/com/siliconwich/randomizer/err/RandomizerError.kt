package com.siliconwich.randomizer.err

import com.github.michaelbull.result.Err
import java.util.*

sealed class RandomizerError : Exception(){
    object NoConstructorFound: RandomizerError()
    object CantRandomizeAbstractClass: RandomizerError()
    object CantApplyRandomizer: RandomizerError()
}
