package com.x12q.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.types.IrType

fun IrCall.withValueArgs(vararg valueArgs:IrExpression): IrFunctionAccessExpression {
    val newArgs = (this.valueArguments + valueArgs).filterNotNull()
    for((index,arg) in newArgs.withIndex()){
        this.putValueArgument(index,arg)
    }
    return this
}

fun IrCall.withTypeArgs(vararg typeArgs: IrType):IrCall{
    val newTypeArgs = (this.typeArguments + typeArgs).filterNotNull()
    for((index,typeArg) in newTypeArgs.withIndex()){
        this.putTypeArgument(index,typeArg)
    }
    return this
}
