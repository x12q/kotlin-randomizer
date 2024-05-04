package com.x12q.randomizer.test_util

import com.squareup.anvil.annotations.MergeComponent
import com.x12q.randomizer.RandomGenerator
import com.x12q.randomizer.di.CommonModule
import com.x12q.randomizer.di.RandomizableAnvilScope
import com.x12q.randomizer.randomizer.di.RandomizerModule
import javax.inject.Singleton

@MergeComponent(
    scope = RandomizableAnvilScope::class,
    modules = [
        CommonModule::class,
        RandomizerModule::class,
        TestModule::class,
    ]
)
@Singleton
interface TestComp {
    fun randomizer(): RandomGenerator
}


