package com.x12q.kotlin.randomizer.ir_plugin.mock_objects

object AlwaysFalseRandomConfig: StaticTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return false
    }
}
