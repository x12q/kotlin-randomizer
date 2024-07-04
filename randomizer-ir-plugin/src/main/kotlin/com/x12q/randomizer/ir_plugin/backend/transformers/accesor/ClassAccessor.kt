package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.functions

abstract class ClassAccessor(
    private val clzz: IrClassSymbol
){
    protected fun zeroAgrFunction(name:String): IrSimpleFunctionSymbol {
        val rt = clzz.functions.firstOrNull { functionSym ->
            functionSym.owner.name.identifier == name && functionSym.owner.valueParameters.isEmpty()
        }
        requireNotNull(rt)
        return rt
    }
}
