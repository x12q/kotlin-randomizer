package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.RandomConfig

class LegalRandomConfig : RandomConfig{
    override fun nextInt(): Int {
        return 1
    }
}
