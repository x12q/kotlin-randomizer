package com.x12q.randomizer.lib

inline fun <reified T:Any> constantRandomizer(value:T):ClassRandomizer<T>{
    return ConstantClassRandomizer.of(value)
}
