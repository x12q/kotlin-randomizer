package com.x12q.randomizer.lib.randomizer.mock_obj

object LegalRandomConfigObject2 : DefaultTestRandomConfig(){
    override fun nextInt(): Int {
        return -super.nextInt()
    }
}
