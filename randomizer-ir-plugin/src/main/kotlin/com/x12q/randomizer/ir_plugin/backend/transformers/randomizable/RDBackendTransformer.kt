package com.x12q.randomizer.ir_plugin.backend.transformers.randomizable

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext

abstract class RDBackendTransformer : IrElementTransformerVoidWithContext() {
    abstract val pluginContext:IrPluginContext
}
