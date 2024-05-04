package com.x12q.randomizer.di

import com.squareup.anvil.annotations.MergeComponent
import com.x12q.randomizer.RandomGenerator
import com.x12q.randomizer.randomizer.di.RandomizerModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
@MergeComponent(
    modules = [
        CommonModule::class,
        RandomizerModule::class,
    ],
    scope = RandomizableAnvilScope::class,
)
interface RDComponent {
    fun random():Random
    fun randomizer(): RandomGenerator

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun setRandom(i:Random):Builder
        fun build():RDComponent
    }
}
