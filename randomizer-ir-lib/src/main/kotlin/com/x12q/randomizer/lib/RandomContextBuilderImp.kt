package com.x12q.randomizer.lib

class RandomContextBuilderImp: RandomContextBuilder {
    private val randomizers:MutableList<ClassRandomizer<*>> = mutableListOf()

    override fun add(randomizer: ClassRandomizer<*>): RandomContextBuilder {
        randomizers.add(randomizer)
        return this
    }

    private var randomConfig:RandomConfig? = null

    override fun setRandomConfig(randomConfig: RandomConfig): RandomContextBuilder {
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
