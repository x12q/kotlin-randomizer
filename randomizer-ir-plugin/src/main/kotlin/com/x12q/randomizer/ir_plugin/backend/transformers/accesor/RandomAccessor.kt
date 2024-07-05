package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.backend.transformers.utils.dotCall
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.toIrConst
import kotlin.random.Random

/**
 * Provide convenient access to kotlin.random.Random member function symbols
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
class RandomAccessor(
    private val randomClass:IrClassSymbol,
    val pluginContext: IrPluginContext,
):ClassAccessor(randomClass) {

    //====

    val nextInt:IrSimpleFunctionSymbol by lazy {
        randomClass.zeroAgrFunction("nextInt")
    }

    fun nextInt(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextInt)
    }

    fun nextInt(receiver: IrExpression, builder: DeclarationIrBuilder): IrCall {
        return receiver.dotCall(builder.irCall(nextInt))
    }

    //====

    val nextLong:IrSimpleFunctionSymbol by lazy {
        randomClass.zeroAgrFunction("nextLong")
    }

    fun nextLong(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextLong)
    }

    //====

    val nextDouble:IrSimpleFunctionSymbol by lazy {
        randomClass.zeroAgrFunction("nextDouble")
    }

    fun nextDouble(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextDouble)
    }

    val nextDoubleUntil:IrSimpleFunctionSymbol by lazy {
        randomClass.oneAgrFunction("nextDouble")
    }

    val nextDoubleBetween:IrSimpleFunctionSymbol by lazy {
        randomClass.oneAgrFunction("nextDouble")
    }

    //====

    val nextFloat:IrSimpleFunctionSymbol by lazy {
        randomClass.zeroAgrFunction("nextFloat")
    }

    fun nextFloat(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextFloat)
    }

    //====

    val nextBoolean:IrSimpleFunctionSymbol by lazy {
        randomClass.zeroAgrFunction("nextBoolean")
    }

    fun nextBoolean(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextBoolean)
    }

    //====
}
