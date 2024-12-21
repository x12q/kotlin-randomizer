package com.x12q.kotlin.randomizer.test.util

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class TestIRGenerationExtension(
    val makeTransformers:(pluginContext:IrPluginContext)->List<IrElementTransformerVoidWithContext>
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        for (transformer in makeTransformers(pluginContext)){
            moduleFragment.transform(transformer, null)
        }
    }
}
