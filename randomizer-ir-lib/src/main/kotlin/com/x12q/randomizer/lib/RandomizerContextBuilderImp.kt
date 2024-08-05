package com.x12q.randomizer.lib

class RandomizerContextBuilderImp: RandomizerContextBuilder {
    private val randomizers:MutableList<ClassRandomizer<*>> = mutableListOf()

    override fun add(randomizer: ClassRandomizer<*>): RandomizerContextBuilder {
        randomizers.add(randomizer)
        return this
    }

    private var randomConfig:RandomConfig? = null

    override fun setRandomConfig(randomConfig: RandomConfig): RandomizerContextBuilder {
        this.randomConfig = randomConfig
        return this
    }

    private var builtRandomizerCollection:RandomizerCollection? = null

    private fun buildRandomizerCollection() {
        if(builtRandomizerCollection == null){
            builtRandomizerCollection = RandomizerCollectionImp(randomizers.associateBy { it.returnType })
        }
    }

    override fun buildContext(): RandomContext {
        val baseRandomConfig = randomConfig ?: RandomConfigImp.default
        if(builtRandomizerCollection == null){
            buildRandomizerCollection()
        }

        return RandomContextImp(
            baseRandomConfig, builtRandomizerCollection!!
        )
    }
}
