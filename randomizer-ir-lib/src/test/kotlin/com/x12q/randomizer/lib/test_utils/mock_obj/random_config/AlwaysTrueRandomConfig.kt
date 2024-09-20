package com.x12q.randomizer.lib.test_utils.mock_obj.random_config

object AlwaysTrueRandomConfig: StaticTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return true
    }
}


