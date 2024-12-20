package com.x12q.kotlin.randomizer.ir_plugin.mock_objects

import com.x12q.kotlin.randomizer.lib.RandomConfig
import kotlin.random.Random

class IllegalRandomConfig (val i:Int): RandomConfig {
    override val random: Random
        get() = TODO("Not yet implemented")
    override val stringSize: IntRange
        get() = TODO("Not yet implemented")
    override val stringCandidates: List<Char>
        get() = TODO("Not yet implemented")
    override val collectionSizeRange: IntRange
        get() = TODO("Not yet implemented")
    override val charRange: CharRange
        get() = TODO("Not yet implemented")
}
