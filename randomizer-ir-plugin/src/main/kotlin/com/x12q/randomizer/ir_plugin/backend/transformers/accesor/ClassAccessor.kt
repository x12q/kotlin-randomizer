package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.backend.transformers.utils.oneAgrFunction
import com.x12q.randomizer.ir_plugin.backend.transformers.utils.twoAgrFunction
import com.x12q.randomizer.ir_plugin.backend.transformers.utils.zeroAgrFunction
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.irCall

abstract class ClassAccessor(
    private val clzz: IrClassSymbol
){
    protected fun zeroAgrFunction(name:String): IrSimpleFunctionSymbol {
        return clzz.zeroAgrFunction(name)
    }

    protected fun DeclarationIrBuilder.zeroAgrFunctionCall(name:String): IrCall {
        return irCall(clzz.zeroAgrFunction(name))
    }

    protected fun oneAgrFunction(name:String): IrSimpleFunctionSymbol {
        return clzz.oneAgrFunction(name)
    }

    protected fun DeclarationIrBuilder.oneAgrFunctionCall(name:String): IrCall {
        return irCall(clzz.oneAgrFunction(name))
    }

    protected fun twoAgrFunction(name:String): IrSimpleFunctionSymbol {
        return clzz.twoAgrFunction(name)
    }


    protected fun DeclarationIrBuilder.twoArgFunctionCall(name:String): IrCall {
        return irCall(clzz.twoAgrFunction(name))
    }
}
