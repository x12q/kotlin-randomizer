package com.x12q.randomizer.ir_plugin.backend.transformers.accessor

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class Function0Accessor @Inject constructor(
    private val pluginContext: IrPluginContext
):ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Function0::class.qualifiedName!!)))) {
            "kotlin.Function0 class is not in the class path."
        }
    }

    private val invokeFunction: IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("invoke")
    }
    fun invokeFunction(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(invokeFunction)
    }
}
