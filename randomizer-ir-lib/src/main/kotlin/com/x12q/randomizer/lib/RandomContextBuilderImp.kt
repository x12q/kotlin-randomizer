package com.x12q.randomizer.lib

import kotlin.reflect.KClass



class RandomContextBuilderImp: RandomContextBuilder {
    private val randomizersMap:MutableMap<KClass<*>, ClassRandomizer<*>> = mutableMapOf()

    override fun add(randomizer: ClassRandomizer<*>): RandomContextBuilder {
        randomizersMap[randomizer.returnType] = randomizer
        return this
    }

    private var _randomConfig:RandomConfig? = null

    override fun setRandomConfig(randomConfig: RandomConfig): RandomContextBuilder {
        this._randomConfig = randomConfig
        addStandardRandomizers(randomConfig)
        return this
    }

    private fun addStandardRandomizers(randomConfig: RandomConfig){
        val stdRdm = listOf(
            factoryRandomizer { randomConfig.nextInt() },
            factoryRandomizer { randomConfig.nextByte() },
            factoryRandomizer { randomConfig.nextLong() },
            factoryRandomizer { randomConfig.nextShort() },

            factoryRandomizer { randomConfig.nextFloat() },
            factoryRandomizer { randomConfig.nextDouble() },
            factoryRandomizer { randomConfig.nextNumber() },

            factoryRandomizer { randomConfig.nextBoolean() },
            factoryRandomizer { randomConfig.nextChar() },

            factoryRandomizer { randomConfig.nextUInt() },
            factoryRandomizer { randomConfig.nextUByte() },
            factoryRandomizer { randomConfig.nextULong() },
            factoryRandomizer { randomConfig.nextUShort() },

            factoryRandomizer { randomConfig.nextStringUUID() },
            factoryRandomizer { randomConfig.nextUnit() },
            factoryRandomizer { randomConfig.nextAny() },
        )
        randomizersMap.putAll(stdRdm.associateBy { it.returnType })
    }

    private var builtRandomizerCollection:RandomizerCollection? = null

    private fun buildRandomizerCollection() {
        if(builtRandomizerCollection == null){
            builtRandomizerCollection = RandomizerCollectionImp(randomizersMap.toMap())
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
