package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.ir_plugin.transformers.di.DaggerP7Component
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.serialization.FirAdditionalMetadataAnnotationsProvider
import org.jetbrains.kotlin.fir.serialization.FirElementAwareStringTable
import org.jetbrains.kotlin.fir.serialization.FirSerializerExtension
import org.jetbrains.kotlin.fir.serialization.constant.ConstValueProvider
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion


class RandomizerModifierIrGenerationExtension(
    private val messageCollector: MessageCollector,
   val enable:Boolean,
) : IrGenerationExtension {

    /**
     * An [IrModuleFragment] is an [IrElement]
     */
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if(enable){
            val comp = DaggerP7Component
                .builder()
                .setIRPluginContext(pluginContext)
                .build()
            val randomizableTransformer2 = comp.randomizableTransformer()
            moduleFragment.transform(randomizableTransformer2,null)
//            val someFunctionTransformer = SomeFunctionTransformer(pluginContext)
//            val dumpCallTransformer = DumpTransformer(pluginContext)
//            val clickableTransformer = ModifierClickableTransformer(pluginContext)
//            moduleFragment.transform(someFunctionTransformer, null)
//            moduleFragment.transform(dumpCallTransformer, null)
//            moduleFragment.transform(clickableTransformer,null)
        }
    }
}




