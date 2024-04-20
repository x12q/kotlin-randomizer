package com.x12q.randomizer.config


object RandomizerConfigFactory{
    fun defaultConfig():RandomizerCollection{
        return RandomizerConfigImp()
    }
}
