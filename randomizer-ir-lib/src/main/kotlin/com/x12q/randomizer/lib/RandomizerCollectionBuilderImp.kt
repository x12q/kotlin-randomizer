package com.x12q.randomizer.lib

class RandomizerCollectionBuilderImp: RandomizerCollectionBuilder {
    private val randomizers:MutableList<ClassRandomizer<*>> = mutableListOf()

    override fun add(randomizer: ClassRandomizer<*>): RandomizerCollectionBuilder {
        randomizers.add(randomizer)
        return this
    }

    private var randomConfig:RandomConfig? = null

    override fun setRandomConfig(randomConfig: RandomConfig): RandomizerCollectionBuilder {
        this.randomConfig = randomConfig
        return this
    }

    private var builtRandomizerCollection:RandomizerCollection? = null

    override fun build(): RandomizerCollection {
        if(builtRandomizerCollection == null){
            builtRandomizerCollection = RandomizerCollectionImp(randomizers.associateBy { it.returnType })
        }
        return builtRandomizerCollection!!
    }

    override fun buildConfig(): RandomConfig {
        val baseRandomConfig = randomConfig ?: RandomConfigImp.default
        if(builtRandomizerCollection == null){
            build()
        }

        return RandomConfigImp(
            random = baseRandomConfig.random,
            collectionSizeRange = baseRandomConfig.collectionSizeRange,
            charRange = baseRandomConfig.charRange,
            randomizerCollection = builtRandomizerCollection!!
        )
    }
}



