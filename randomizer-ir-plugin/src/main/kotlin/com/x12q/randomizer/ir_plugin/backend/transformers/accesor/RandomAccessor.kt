package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.backend.transformers.utils.dotCall
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

/**
 * Provide convenient access to kotlin.random.Random member function symbols
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
class RandomAccessor(
    private val randomClass:IrClassSymbol
):ClassAccessor(randomClass) {
    val nextInt:IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextInt")
    }

    fun nextInt(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextInt)
    }

    fun nextInt(receiver: IrExpression, builder: DeclarationIrBuilder): IrCall {
        return receiver.dotCall(builder.irCall(nextInt))
    }

    val nextLong:IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextLong")
    }

    fun nextLong(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextLong)
    }
}
