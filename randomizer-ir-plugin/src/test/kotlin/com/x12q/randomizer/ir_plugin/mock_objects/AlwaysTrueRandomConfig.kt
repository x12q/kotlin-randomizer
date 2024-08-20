package com.x12q.randomizer.ir_plugin.mock_objects

object AlwaysTrueRandomConfig: DefaultTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return true
    }
}


