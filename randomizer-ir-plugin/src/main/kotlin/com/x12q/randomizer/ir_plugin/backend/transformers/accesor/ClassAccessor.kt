package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.functions

abstract class ClassAccessor(
    private val clzz: IrClassSymbol
){
    protected fun IrClassSymbol.zeroAgrFunction(name:String): IrSimpleFunctionSymbol {
        val rt = clzz.functions.firstOrNull { functionSym ->
            functionSym.owner.name.identifier == name && functionSym.owner.valueParameters.isEmpty()
        }
        requireNotNull(rt){"zero-arg function $name() does not exist"}
        return rt
    }

    protected fun IrClassSymbol.oneAgrFunction(name:String): IrSimpleFunctionSymbol {
        val rt = clzz.functions.firstOrNull { functionSym ->
            functionSym.owner.name.identifier == name && functionSym.owner.valueParameters.size==1
        }
        requireNotNull(rt){"one-arg function $name() does not exist"}
        return rt
    }

    protected fun IrClassSymbol.twoAgrFunction(name:String): IrSimpleFunctionSymbol {
        val rt = clzz.functions.firstOrNull { functionSym ->
            functionSym.owner.name.identifier == name && functionSym.owner.valueParameters.size==2
        }
        requireNotNull(rt){"two-arg function $name() does not exist"}
        return rt
    }
}
