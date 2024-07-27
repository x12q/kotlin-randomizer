package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import javax.inject.Inject

class ClassRandomizerCollectionBuilderAccessor @Inject constructor(
    private val basicAccessor: BasicAccessor
):ClassAccessor() {
    override val clzz: IrClassSymbol = basicAccessor.ClassRandomizerCollectionBuilder_Interface

    fun buildFunction(builder: DeclarationIrBuilder): IrCall {
        return builder.zeroAgrFunctionCall("build")
    }

    fun addFunction(builder: DeclarationIrBuilder):IrCall{
        return builder.oneAgrFunctionCall("add")
    }
}
