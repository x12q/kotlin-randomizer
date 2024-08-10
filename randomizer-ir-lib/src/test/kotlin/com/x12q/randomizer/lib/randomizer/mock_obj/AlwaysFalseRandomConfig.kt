package com.x12q.randomizer.lib.randomizer.mock_obj

object AlwaysFalseRandomConfig: DefaultTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return false
    }
}
