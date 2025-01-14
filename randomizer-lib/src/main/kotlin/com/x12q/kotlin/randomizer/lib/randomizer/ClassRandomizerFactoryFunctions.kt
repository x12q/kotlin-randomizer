package com.x12q.kotlin.randomizer.lib.randomizer

inline fun <reified T> constantRandomizer(value:T): ClassRandomizer<T> {
    return ConstantRandomizer.of(value)
}

inline fun <reified T> constantRandomizer(noinline makeValue:()->T): ClassRandomizer<T> {
    return LazyConstantRandomizer.of(makeValue)
}

inline fun <reified T> factoryRandomizer(noinline makeRandom:()->T): ClassRandomizer<T> {
    return FactoryClassRandomizer.of(makeRandom)
}
