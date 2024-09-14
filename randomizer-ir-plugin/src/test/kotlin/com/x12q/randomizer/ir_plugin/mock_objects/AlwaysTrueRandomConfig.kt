package com.x12q.randomizer.ir_plugin.mock_objects

object AlwaysTrueRandomConfig: StaticTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return true
    }
}


