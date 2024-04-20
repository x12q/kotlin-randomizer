package com.x12q.randomizer.di

import com.squareup.anvil.annotations.MergeComponent
import com.x12q.randomizer.Randomizer
import com.x12q.randomizer.randomizer.di.DefaultRandom
import com.x12q.randomizer.randomizer.di.RandomizerModule
import kotlin.random.Random

@RDSingleton
@MergeComponent(
    modules = [
        RDModule::class,
        RandomizerModule::class,
    ],
    scope = RandomizableAnvilScope::class
)
interface RDComponent {
    @DefaultRandom
    fun random():Random

    fun randomizer(): Randomizer
}
