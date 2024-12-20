package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer
import com.x12q.kotlin.randomizer.lib.randomizer.factoryRandomizer
import java.util.Date


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
        makePrimitiveRandomizers(randomConfig)
        return this
    }


    override fun makePrimitiveRandomizers(randomConfig: RandomConfig) {
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
            factoryRandomizer { Date() }
        )
        randomizersMap.putAll(stdRdm.associateBy { it.returnType })
    }

    private val tier2RandomizerFactoryFunctionList: MutableList<(RandomContext) -> ClassRandomizer<*>> = mutableListOf()

    override fun addTier2Randomizer(makeRandomizer: RandomContext.() -> ClassRandomizer<*>): RandomContextBuilderImp {
        tier2RandomizerFactoryFunctionList.add(makeRandomizer)
        return this
    }

    private var builtRandomizerCollection: RandomizerCollection? = null

    /**
     * Tier1 randomizers are simply normal randomizers.
     */
    private fun addTier1Randomizers() {
        val rdCollection = getOrInitRandomizerCollection()
        randomizersMap.forEach { (typeKey, randomizer) ->
            rdCollection.add(typeKey, randomizer)
        }
    }

    override val randomConfig: RandomConfig
        get() = requireNotNull(_randomConfig) {
            "_randomConfig is not set yet. This is a bug by the developer."
        }

    /**
     * Tier2 randomizers are randomizer that has access to the final [RandomContext] obj
     */
    private fun addTier2Randomizers(rdContext: RandomContext) {
        val rdCollection = getOrInitRandomizerCollection()
        for (makeRandomizer in tier2RandomizerFactoryFunctionList) {
            val t2Randomizer = makeRandomizer(rdContext)
            rdCollection.add(t2Randomizer.returnType, t2Randomizer)
        }
    }

    private fun getOrInitRandomizerCollection(): RandomizerCollection {
        val rdCollection = (builtRandomizerCollection ?: MutableRandomizerCollection(emptyMap())).also {
            builtRandomizerCollection = it
        }
        return rdCollection
    }

    override fun build(): RandomContext {
        val baseRandomConfig = _randomConfig ?: RandomConfig.default
        val rdCollection = getOrInitRandomizerCollection()
        val rt = RandomContextImp(baseRandomConfig, rdCollection)
        addTier1Randomizers()
        addTier2Randomizers(rt)
        return rt
    }

}

