package com.x12q.kotlin.randomizer.ir_plugin.mock_objects

import com.x12q.kotlin.randomizer.lib.ForKotlinRandomizerGeneratedCodeOnly
import com.x12q.kotlin.randomizer.lib.RandomConfig
import kotlin.random.Random

/**
 * A test random config that can be reset to initial state.
 * Use case: so that it can re-generate a consistent sequence of objects
 */
class TestRandomConfig : RandomConfig  {
    override var random: Random = Random(123)
    override val stringSize: IntRange = 1 .. 20
    @ForKotlinRandomizerGeneratedCodeOnly
    override fun randomizableCandidateIndex(candidateCount: Int): Int {
        return 0
    }

    override val collectionSizeRange: IntRange = 6 .. 6

    override fun randomCollectionSize(): Int {
        return 3
    }

    override val charRange: CharRange =  'A' .. 'z'
    override val stringCandidates: List<Char> = charRange.toList()
    /**
     * Recreate the random obj
     */
    fun resetRandomState(){
        random = Random(123)
    }
}
