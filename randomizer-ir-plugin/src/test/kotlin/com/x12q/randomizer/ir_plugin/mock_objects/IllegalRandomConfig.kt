package com.x12q.randomizer.ir_plugin.mock_objects

import com.x12q.randomizer.lib.ClassRandomizer
import com.x12q.randomizer.lib.RandomConfig
import kotlin.random.Random
import kotlin.reflect.KClass

class IllegalRandomConfig (val i:Int): RandomConfig {
    override val random: Random
        get() = TODO("Not yet implemented")
    override val collectionSizeRange: IntRange
        get() = TODO("Not yet implemented")
    override val charRange: CharRange
        get() = TODO("Not yet implemented")
}
