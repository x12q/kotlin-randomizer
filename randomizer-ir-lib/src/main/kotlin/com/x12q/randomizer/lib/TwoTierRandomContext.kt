package com.x12q.randomizer.lib

import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * An internal random context that is used only by the randomizer plugin
 */
class TwoTierRandomContext(
    private val tier1: RandomContext,
    private val tier2: RandomContext,
) : RandomContext {
    init {
        if (tier1.randomConfig != tier2.randomConfig) {
            throw IllegalArgumentException("TwoLayerRandomContext can only hold 2 contexts having the same RandomConfig")
        }
    }

    override val randomConfig: RandomConfig = tier1.randomConfig
    override val random: Random = tier1.random
    override val collectionSizeRange: IntRange = randomConfig.collectionSizeRange
    override val charRange: CharRange = randomConfig.charRange

    override val randomizersMap: Map<KClass<*>, ClassRandomizer<*>> = tier2.randomizersMap + tier1.randomizersMap

    override fun getRandomizerForClass(clazz: KClass<*>): ClassRandomizer<*>? {
        return tier1.getRandomizerForClass(clazz) ?: tier2.getRandomizerForClass(clazz)
    }
}
