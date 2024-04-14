package com.siliconwich.randomizer.config


object RandomizerConfigFactory{
    fun defaultConfig():RandomizerConfig{
        return RandomizerConfigImp()
    }
}
