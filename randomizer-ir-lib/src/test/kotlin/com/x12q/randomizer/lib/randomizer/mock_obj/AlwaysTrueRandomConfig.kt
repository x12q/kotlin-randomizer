package com.x12q.randomizer.lib.randomizer.mock_obj

object AlwaysTrueRandomConfig: DefaultTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return true
    }
}


