package com.siliconwich.randomizer

sealed class RandomizerError{
    object NoConstructorFound:RandomizerError()
}
