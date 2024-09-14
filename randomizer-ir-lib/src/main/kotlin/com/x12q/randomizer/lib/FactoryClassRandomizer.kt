package com.x12q.randomizer.lib

/**
 * A class randomizer that run a factory function to produce random values
 */
class FactoryClassRandomizer<T : Any>(
    val makeRandom: () -> T,
    override val returnType: TypeKey
) : ClassRandomizer<T> {
    override fun random(): T {
        return makeRandom()
    }

    companion object {
        inline fun <reified T : Any> of(noinline makeRandom: () -> T): FactoryClassRandomizer<T> {
            return FactoryClassRandomizer(makeRandom, TypeKey.of<T>())
        }
    }
}
