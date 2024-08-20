package com.x12q.randomizer.lib


object RandomContextBuilderFunctions{
    inline fun <reified T : Any> RandomContextBuilder.constant(value:T): RandomContextBuilder {
        return add(constantRandomizer(value))
    }

    inline fun <reified T : Any> RandomContextBuilder.factory(noinline makeRandom:()->T): RandomContextBuilder {
        return add(factoryRandomizer(makeRandom))
    }
}
