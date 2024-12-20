package com.x12q.kotlin.randomizer.ir_plugin.backend

import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.RandomizableBackendTransformer
import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.di.DaggerRandomizerComponent
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


class RDIrGenerationExtension(
    val enable: Boolean = true,
) : IrGenerationExtension {

    /**
     * An [IrModuleFragment] is an [IrElement]
     */
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        if (enable) {
            val comp = DaggerRandomizerComponent
                .builder()
                .setIRPluginContext(pluginContext)
                .build()
            val randomizableTransformer2: RandomizableBackendTransformer = comp.randomizableTransformer()
            moduleFragment.transform(randomizableTransformer2, null)
        }
    }
}




