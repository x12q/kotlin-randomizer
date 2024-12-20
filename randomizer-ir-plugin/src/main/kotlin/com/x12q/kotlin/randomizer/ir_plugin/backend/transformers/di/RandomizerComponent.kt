package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.di

import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.RandomizableBackendTransformer
import dagger.BindsInstance
import dagger.Component
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import javax.inject.Singleton


@Singleton
@Component
interface RandomizerComponent {

    fun randomizableTransformer(): RandomizableBackendTransformer

    @Component.Builder
    interface Builder {
        fun setIRPluginContext( @BindsInstance pluginContext: IrPluginContext): Builder
        fun build(): RandomizerComponent
    }
}
