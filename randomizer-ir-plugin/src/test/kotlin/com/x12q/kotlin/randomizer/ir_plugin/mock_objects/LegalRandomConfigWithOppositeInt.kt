package com.x12q.kotlin.randomizer.ir_plugin.mock_objects

object LegalRandomConfigWithOppositeInt : StaticTestRandomConfig(){
    override fun nextInt(): Int {
        return -super.nextInt()
    }
}
