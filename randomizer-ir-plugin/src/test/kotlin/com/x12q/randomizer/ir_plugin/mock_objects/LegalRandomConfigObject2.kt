package com.x12q.randomizer.ir_plugin.mock_objects

import com.x12q.randomizer.lib.RandomConfig
import kotlin.random.Random

object LegalRandomConfigObject2 : DefaultTestRandomConfig(){
    override fun nextInt(): Int {
        return -super.nextInt()
    }
}
