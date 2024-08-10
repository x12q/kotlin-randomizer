package com.x12q.randomizer.lib

class RandomContextBuilderImp: RandomContextBuilder {
    private val randomizers:MutableList<ClassRandomizer<*>> = mutableListOf()

    override fun add(randomizer: ClassRandomizer<*>): RandomContextBuilder {
        randomizers.add(randomizer)
        return this
    }

    private var _randomConfig:RandomConfig? = null

    override fun setRandomConfig(randomConfig: RandomConfig): RandomContextBuilder {
        this._randomConfig = randomConfig
        return this
    }

    private var builtRandomizerCollection:RandomizerCollection? = null

    private fun buildRandomizerCollection() {
        if(builtRandomizerCollection == null){
            builtRandomizerCollection = RandomizerCollectionImp(randomizers.associateBy { it.returnType })
        }
    }

    override val randomConfig: RandomConfig
        get() = requireNotNull(_randomConfig){
            "_randomConfig is not set yet. This is a bug by the developer."
        }

    override fun buildContext(): RandomContext {
        val baseRandomConfig = _randomConfig ?: RandomConfigImp.default
        if(builtRandomizerCollection == null){
            buildRandomizerCollection()
        }

        return RandomContextImp(
            baseRandomConfig, builtRandomizerCollection!!
        )
    }
}
