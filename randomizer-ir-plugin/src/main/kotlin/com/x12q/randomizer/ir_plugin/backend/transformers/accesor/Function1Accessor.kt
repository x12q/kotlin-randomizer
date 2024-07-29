package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class Function1Accessor @Inject constructor(
    private val pluginContext: IrPluginContext
):ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Function1::class.qualifiedName!!)))) {
            "kotlin.Function1 class is not in the class path."
        }
    }

    private val invokeFunction: IrSimpleFunctionSymbol by lazy {
        oneAgrFunction("invoke")
    }
    fun invokeFunction(builder: IrBuilderWithScope):IrCall{
        return builder.irCall(invokeFunction)
    }

}
