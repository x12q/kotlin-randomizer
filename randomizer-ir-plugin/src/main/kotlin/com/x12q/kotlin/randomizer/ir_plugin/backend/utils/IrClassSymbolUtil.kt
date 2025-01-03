package com.x12q.kotlin.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.functions

fun IrClassSymbol.isAnnotatedWithRandomizable():Boolean{
    return this.owner.isAnnotatedWithRandomizable()
}


fun IrClassSymbol.zeroAgrFunction(name:String): IrSimpleFunctionSymbol {
    val rt = this.functions.firstOrNull { functionSym ->
        functionSym.owner.name.identifier == name && functionSym.owner.valueParameters.isEmpty()
    }
    requireNotNull(rt){"zero-arg function $name() does not exist"}
    return rt
}

fun IrClassSymbol.oneAgrFunction(name:String): IrSimpleFunctionSymbol {
    val rt = this.functions.firstOrNull { functionSym ->
        functionSym.owner.name.identifier == name && functionSym.owner.valueParameters.size==1
    }
    requireNotNull(rt){"one-arg function $name() does not exist"}
    return rt
}

fun IrClassSymbol.twoAgrFunction(name:String): IrSimpleFunctionSymbol {
    val rt = this.functions.firstOrNull { functionSym ->
        functionSym.owner.name.identifier == name && functionSym.owner.valueParameters.size==2
    }
    requireNotNull(rt){"two-arg function $name() does not exist"}
    return rt
}
