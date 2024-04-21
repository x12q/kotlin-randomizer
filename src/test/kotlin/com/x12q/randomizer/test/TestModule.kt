package com.x12q.randomizer.test

import dagger.Module
import dagger.Provides
import kotlin.random.Random

@Module
interface TestModule{
    companion object{
        @Provides
        fun random(): Random {
            return Random
        }
    }
}
