package com.x12q.randomizer.randomizer.di

import com.x12q.randomizer.di.RDSingleton
import dagger.Module
import dagger.Provides
import kotlin.random.Random

@Module
interface RandomizerModule {
    companion object {
        @RDSingleton
        @Provides
        @DefaultRandom
        fun random(): Random {
            return Random(111)
        }
    }
}
