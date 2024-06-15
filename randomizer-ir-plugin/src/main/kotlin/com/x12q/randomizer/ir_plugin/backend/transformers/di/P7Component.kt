package com.x12q.randomizer.ir_plugin.backend.transformers.di

import com.x12q.randomizer.ir_plugin.backend.transformers.randomizable.RandomizableTransformer
//import com.squareup.anvil.annotations.MergeComponent
import com.x12q.randomizer.ir_plugin.backend.transformers.randomizable.RandomizableIRTransformer2
import dagger.BindsInstance
import dagger.Component
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import javax.inject.Singleton


@Singleton
//@MergeComponent(
//    scope = P7AnvilScope::class,
//)
@Component
interface P7Component {

    fun randomizableTransformer(): RandomizableTransformer
    fun randomizableTransformer2(): RandomizableIRTransformer2

    @Component.Builder
    interface Builder {
        fun setIRPluginContext( @BindsInstance pluginContext: IrPluginContext): Builder
        fun build(): P7Component
    }
}
