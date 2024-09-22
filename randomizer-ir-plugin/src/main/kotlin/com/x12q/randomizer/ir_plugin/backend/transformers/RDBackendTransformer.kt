package com.x12q.randomizer.ir_plugin.backend.transformers

import org.jetbrains.kotlin.backend.common.ClassLoweringPass
import org.jetbrains.kotlin.backend.common.DeclarationTransformer
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext

/**
 * This is just a signature interface
 */
abstract class RDBackendTransformer : IrElementTransformerVoidWithContext() {
    abstract val pluginContext:IrPluginContext
}
