package com.x12q.randomizer.ir_plugin.mock_objects

object AlwaysFalseRandomConfig: DefaultTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return false
    }
}
