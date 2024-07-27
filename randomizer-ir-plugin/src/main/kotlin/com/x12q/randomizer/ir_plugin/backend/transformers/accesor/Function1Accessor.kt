package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import javax.inject.Inject

class Function1Accessor @Inject constructor(
    private val basicAccessor: BasicAccessor
):ClassAccessor() {
    override val clzz: IrClassSymbol by lazy { basicAccessor.Function1_Class }

    private val invokeFunction: IrSimpleFunctionSymbol by lazy {
        oneAgrFunction("invoke")
    }
    fun invokeFunction(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(invokeFunction)
    }

}
