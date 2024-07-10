package com.x12q.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression


fun IrExpression.dotCall(irCall: IrCall): IrCall {
    irCall.dispatchReceiver = this
    return irCall
}

fun IrExpression.dotCall(irCall:()->IrCall): IrCall {
    val ir = irCall()
    ir.dispatchReceiver = this
    return ir
}
