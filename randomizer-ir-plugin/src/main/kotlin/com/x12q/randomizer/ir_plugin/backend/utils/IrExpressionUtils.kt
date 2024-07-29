package com.x12q.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression

fun IrExpression.dotCallExtensionFunction(irCall: IrCall): IrCall {
    irCall.extensionReceiver = this
    return irCall
}


fun IrExpression.dotCall(irCall: IrCall): IrCall {
    irCall.dispatchReceiver = this
    return irCall
}

fun IrExpression.dotCall(irCall:()->IrCall): IrCall {
    return dotCall(irCall())
}

fun IrCall.args(vararg valueArgs:IrExpression):IrFunctionAccessExpression{
    for((index,arg) in valueArgs.withIndex()){
        this.putValueArgument(index,arg)
    }
    return this
}


fun IrExpression.dotCall(irCall: IrFunctionAccessExpression): IrFunctionAccessExpression {
    irCall.dispatchReceiver = this
    return irCall
}

fun IrExpression.extensionDotCall(irCall: IrFunctionAccessExpression): IrFunctionAccessExpression {
    irCall.extensionReceiver = this
    return irCall
}

fun IrExpression.dotCall(irCall:()->IrFunctionAccessExpression): IrFunctionAccessExpression {
    return this.dotCall(irCall())
}

fun IrExpression.extensionDotCall(irCall:()->IrFunctionAccessExpression): IrFunctionAccessExpression {
    return this.extensionDotCall(irCall())
}

fun IrFunctionAccessExpression.args(vararg valueArgs:IrExpression):IrFunctionAccessExpression{
    for((index,arg) in valueArgs.withIndex()){
        this.putValueArgument(index,arg)
    }
    return this
}

