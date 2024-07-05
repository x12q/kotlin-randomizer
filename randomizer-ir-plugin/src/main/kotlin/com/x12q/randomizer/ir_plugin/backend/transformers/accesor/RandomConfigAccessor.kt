package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.backend.transformers.utils.dotCall
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.getPropertyGetter

class RandomConfigAccessor(
    private val randomConfigClass: IrClassSymbol
) : ClassAccessor(randomConfigClass) {

    val random by lazy {
        randomConfigClass.getPropertyGetter("random")!!
    }

    fun random(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(random)
    }

    val nextByte by lazy {
        randomConfigClass.zeroAgrFunction("nextByte")
    }

    fun nextByte(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextByte)
    }

    val nextChar by lazy {
        randomConfigClass.zeroAgrFunction("nextChar")
    }

    fun nextChar(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextChar)
    }

}

