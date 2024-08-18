package com.x12q.randomizer.ir_plugin.mock_objects

object NullRandomConfig : DefaultTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return false
    }
}
