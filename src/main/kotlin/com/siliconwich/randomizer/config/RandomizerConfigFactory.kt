package com.siliconwich.randomizer.config


object RandomizerConfigFactory{
    fun defaultConfig():RandomizerCollection{
        return RandomizerConfigImp()
    }
}
