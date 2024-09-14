package com.x12q.randomizer.ir_plugin.mock_objects

import com.x12q.randomizer.lib.RandomConfig
import kotlin.random.Random

class LegalRandomConfig : DefaultTestRandomConfig(){
    override val random: Random = Random(123)
}

