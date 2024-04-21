package com.x12q.randomizer.test

import com.squareup.anvil.annotations.MergeComponent
import com.x12q.randomizer.Randomizer
import com.x12q.randomizer.di.CommonModule
import com.x12q.randomizer.di.RandomizableAnvilScope
import com.x12q.randomizer.randomizer.di.RandomizerModule

@MergeComponent(
    scope = RandomizableAnvilScope::class,
    modules = [
        CommonModule::class,
        RandomizerModule::class,
        TestModule::class,
    ]
)
interface TestComp {
    fun randomizer(): Randomizer
}


