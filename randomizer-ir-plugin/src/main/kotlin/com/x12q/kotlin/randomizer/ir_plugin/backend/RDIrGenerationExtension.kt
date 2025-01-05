package com.x12q.kotlin.randomizer.ir_plugin.backend

import com.x12q.kotlin.randomizer.ir_plugin.backend.di.DaggerRandomizerComponent
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


class RDIrGenerationExtension(
    val enable: Boolean = false,
) : IrGenerationExtension {

    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        if (enable) {
            val comp = DaggerRandomizerComponent
                .builder()
                .setIRPluginContext(pluginContext)
                .build()
            val backendTransformer: RandomizableBackendTransformer = comp.randomizableTransformer()
            moduleFragment.transform(backendTransformer, null)
        }
    }
}




