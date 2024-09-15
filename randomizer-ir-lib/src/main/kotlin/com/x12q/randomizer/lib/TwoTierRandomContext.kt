package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.ClassRandomizer
import kotlin.random.Random

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
    override val stringSize: IntRange = tier1.stringSize
    override val stringCandidates: List<Char> = tier1.stringCandidates
    override val collectionSizeRange: IntRange = randomConfig.collectionSizeRange
    override val charRange: CharRange = randomConfig.charRange
    override val randomizersMap: Map<TypeKey, ClassRandomizer<*>> = tier2.randomizersMap + tier1.randomizersMap
    override fun add(key: TypeKey, randomizer: ClassRandomizer<*>): RandomizerCollection {
        throw UnsupportedOperationException()
    }
}
