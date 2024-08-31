package com.x12q.randomizer.lib

inline fun <reified T:Any> constantRandomizer(value:T):ClassRandomizer<T>{
    return ConstantClassRandomizer.of(value)
}

inline fun <reified T:Any> factoryRandomizer(noinline makeRandom:()->T):ClassRandomizer<T>{
    return FactoryClassRandomizer.of(makeRandom)
}


inline fun qwe(){
    constantRandomizer<Int>(123)
}
