package com.x12q.randomizer.test_util

import com.squareup.anvil.annotations.MergeComponent
import com.x12q.randomizer.RandomGenerator
import com.x12q.randomizer.di.RandomizableAnvilScope
import javax.inject.Singleton

@MergeComponent(
    scope = RandomizableAnvilScope::class,
    modules = [
        TestModule::class,
    ]
)
@Singleton
interface TestComp {
    fun randomizer(): RandomGenerator
}


