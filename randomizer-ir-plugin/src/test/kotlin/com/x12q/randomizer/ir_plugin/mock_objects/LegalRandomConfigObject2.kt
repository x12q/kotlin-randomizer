package com.x12q.randomizer.ir_plugin.mock_objects

object LegalRandomConfigObject2 : StaticTestRandomConfig(){
    override fun nextInt(): Int {
        return -super.nextInt()
    }
}
