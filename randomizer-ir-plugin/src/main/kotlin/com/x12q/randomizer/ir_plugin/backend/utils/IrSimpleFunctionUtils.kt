package com.x12q.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addExtensionReceiver
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.statements

/**
 * Make an identical clone of [originalFunction] object. The original object is kept intact.
 */
fun IrSimpleFunction.cloneFunction(
    pluginContext: IrPluginContext,
): IrSimpleFunction {
    val originalFunction = this
    val rt = pluginContext.irFactory.buildFun {
        name = originalFunction.name
        origin = originalFunction.origin
        visibility = originalFunction.visibility
        returnType = originalFunction.returnType
        modality = originalFunction.modality
        isSuspend = originalFunction.isSuspend
    }.apply {
        parent = originalFunction.parent
        originalFunction.extensionReceiverParameter?.type?.also {
            addExtensionReceiver(it)
        }
        val builder = DeclarationIrBuilder(
            generatorContext = pluginContext,
            symbol = this.symbol,
        )
        body = builder.irBlockBody {
            originalFunction.body?.statements?.forEach { stm ->
                +stm
            }
        }
    }
    return rt

}
