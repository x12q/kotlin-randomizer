package com.x12q.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.types.IrType


fun IrExpression.dotCall(irCall: IrCall): IrCall {
    irCall.dispatchReceiver = this
    return irCall
}

fun IrExpression.dotCall(irCall:()->IrCall): IrCall {
    return dotCall(irCall())
}


fun IrExpression.dotCall(irCall: IrFunctionAccessExpression): IrFunctionAccessExpression {
    irCall.dispatchReceiver = this
    return irCall
}

fun IrExpression.dotCall(irCall:()->IrFunctionAccessExpression): IrFunctionAccessExpression {
    return this.dotCall(irCall())
}

fun IrExpression.extensionDotCall(irCall: IrFunctionAccessExpression): IrFunctionAccessExpression {
    irCall.extensionReceiver = this
    return irCall
}
fun IrExpression.extensionDotCall(irCall: IrCall): IrCall {
    irCall.extensionReceiver = this
    return irCall
}

fun IrExpression.extensionDotCall(irCall:()->IrFunctionAccessExpression): IrFunctionAccessExpression {
    return this.extensionDotCall(irCall())
}

fun IrFunctionAccessExpression.withValueArgs(vararg valueArgs:IrExpression):IrFunctionAccessExpression{
    for((index,arg) in valueArgs.withIndex()){
        this.putValueArgument(index,arg)
    }
    return this
}

fun IrFunctionAccessExpression.withValueArgs(args:List<IrExpression>):IrFunctionAccessExpression{
    return this.withValueArgs(*args.toTypedArray())
}


