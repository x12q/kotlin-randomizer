package com.x12q.randomizer.lib

import kotlin.reflect.KClass


class TwoTierRandomContextBuilder(
    val tier1Context:RandomContext,
) {
    private val tier2Builder = RandomContextBuilderImp()

    fun add(makeRandomizer: (RandomContext)->ClassRandomizer<*>): TwoTierRandomContextBuilder {
        val randomizer = makeRandomizer(tier1Context)
        tier2Builder.add(randomizer)
        return this
    }

    fun build(): TwoTierRandomContext {
        val tier2 = tier2Builder.buildContext()
        return TwoTierRandomContext(
            tier1Context,tier2
        )
    }
}

