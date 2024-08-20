package com.x12q.randomizer.di

import com.squareup.anvil.annotations.MergeComponent
import com.x12q.randomizer.RandomGenerator
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
@MergeComponent(
    scope = RandomizerAnvilScope::class,
)
interface RandomizerComponent {
    fun random():Random
    fun randomizer(): RandomGenerator

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun setRandom(i:Random):Builder
        fun build():RandomizerComponent
    }
}
