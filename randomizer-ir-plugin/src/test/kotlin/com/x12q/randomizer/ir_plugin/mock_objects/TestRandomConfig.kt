package com.x12q.randomizer.ir_plugin.mock_objects

import com.x12q.randomizer.lib.RandomConfig
import kotlin.random.Random

class TestRandomConfig : RandomConfig  {
    override var random: Random = Random(123)
    override val collectionSizeRange: IntRange = 6 .. 6

    override fun randomCollectionSize(): Int {
        return 6
    }

    override val charRange: CharRange =  'A' .. 'z'

    /**
     * Recreate the random obj
     */
    fun reset(){
        random = Random(123)
    }
}
