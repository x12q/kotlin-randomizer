package com.x12q.randomizer.test.util

//import com.x12q.randomizer.ir_plugin.transformers.di.DaggerP7Component
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class TestIrGenerationExtension(
    val transformers:(pluginContext:IrPluginContext)->List<IrElementTransformerVoidWithContext>
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        for (transformer in transformers(pluginContext)){
            moduleFragment.transform(transformer, null)
        }
    }
}
