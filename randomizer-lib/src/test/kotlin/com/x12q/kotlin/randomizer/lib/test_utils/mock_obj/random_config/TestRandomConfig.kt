package com.x12q.kotlin.randomizer.lib.test_utils.mock_obj.random_config

import com.x12q.kotlin.randomizer.lib.RandomConfig
import kotlin.random.Random

/**
 * A test random config that can be reset to initial state.
 * Use case: so that it can re-generate a random sequence of object
 */
class TestRandomConfig : RandomConfig {
    override var random: Random = Random(123)
    override val collectionSizeRange: IntRange = 6 .. 6

    override fun randomCollectionSize(): Int {
        return 6
    }

    override val charRange: CharRange =  'A' .. 'z'
    override val stringSize: IntRange = 1 .. 20
    override val stringCandidates: List<Char> = charRange.toList()

    /**
     * Recreate the random obj
     */
    fun reset(){
        random = Random(123)
    }
}