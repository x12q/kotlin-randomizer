package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.ClassRandomizer
import com.x12q.randomizer.lib.randomizer.factoryRandomizer


class RandomContextBuilderImp : RandomContextBuilder {
    private val randomizersMap: MutableMap<TypeKey, ClassRandomizer<*>> = mutableMapOf()

    override fun add(randomizer: ClassRandomizer<*>): RandomContextBuilder {
        randomizersMap[randomizer.returnType] = randomizer
        return this
    }

    private var _randomConfig: RandomConfig? = null

    override fun setRandomConfig(randomConfig: RandomConfig): RandomContextBuilder {
        this._randomConfig = randomConfig
        return this
    }

    override fun setRandomConfigAndGenerateStandardRandomizers(randomConfig: RandomConfig): RandomContextBuilder {
        setRandomConfig(randomConfig)
        generateStandardRandomizers(randomConfig)
        return this
    }


    override fun generateStandardRandomizers(randomConfig: RandomConfig) {
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

            factoryRandomizer { randomConfig.nextString() },
            factoryRandomizer { randomConfig.nextUnit() },
            factoryRandomizer { randomConfig.nextAny() },
        )
        randomizersMap.putAll(stdRdm.associateBy { it.returnType })
    }

    private val tier2RandomizerFactoryFunctionList: MutableList<(RandomContext) -> ClassRandomizer<*>> = mutableListOf()

    override fun addForTier2(makeRandomizer: (RandomContext.() -> ClassRandomizer<*>)?): RandomContextBuilderImp {
        if (makeRandomizer != null) {
            tier2RandomizerFactoryFunctionList.add(makeRandomizer)
        }
        return this
    }

    private var builtRandomizerCollection: RandomizerCollection? = null

    private fun buildRandomizerCollection() {
        if (builtRandomizerCollection == null) {
            // builtRandomizerCollection = RandomizerCollectionImp(randomizersMap.toMap())
            builtRandomizerCollection = MutableRandomizerCollection(randomizersMap.toMap())
        }
    }

    override val randomConfig: RandomConfig
        get() = requireNotNull(_randomConfig) {
            "_randomConfig is not set yet. This is a bug by the developer."
        }


    override fun build(): RandomContext {
        val baseRandomConfig = _randomConfig ?: RandomConfigImp.default

        if (builtRandomizerCollection == null) {
            buildRandomizerCollection()
        }

        val rdCollection = builtRandomizerCollection!!

        val randomContext1 = RandomContextImp(
            baseRandomConfig, rdCollection
        )

        if (tier2RandomizerFactoryFunctionList.isNotEmpty()) {
            for (makeRandomizer in tier2RandomizerFactoryFunctionList) {
                val t2Randomizer = makeRandomizer(randomContext1)
                rdCollection.add(t2Randomizer.returnType, t2Randomizer)
            }
        }
        return randomContext1
    }

}

