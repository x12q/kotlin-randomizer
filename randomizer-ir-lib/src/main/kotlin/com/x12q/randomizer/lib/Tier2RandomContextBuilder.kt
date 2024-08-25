package com.x12q.randomizer.lib


class Tier2RandomContextBuilder(
    private val tier1Context:RandomContext,
) {
    private val tier2Builder = RandomContextBuilderImp()

    fun invokeAndAdd(makeRandomizer: (RandomContext)->ClassRandomizer<*>): Tier2RandomContextBuilder {
        val randomizer = makeRandomizer(tier1Context)
        tier2Builder.add(randomizer)
        return this
    }

    fun build(): TwoTierRandomContext {
        val tier2 = tier2Builder
            .setRandomConfig(tier1Context.randomConfig)
            .buildContext()
        return TwoTierRandomContext(
            tier1Context,tier2
        )
    }
}

