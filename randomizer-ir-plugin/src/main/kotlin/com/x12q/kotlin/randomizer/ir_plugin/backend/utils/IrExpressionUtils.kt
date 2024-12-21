package com.x12q.kotlin.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irIfNull
import org.jetbrains.kotlin.ir.builders.irNull
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.types.IrType


fun IrExpression.nullSafeDotCall(
    irCall: IrCall,
    returnType:IrType,
    builder: DeclarationIrBuilder,
    valueArgs: List<IrExpression>,
    typeArgs: List<IrType> = emptyList()
): IrExpression {
    val safeVar = this@nullSafeDotCall

    val rt = builder.irBlock {
        +irIfNull(
            type = returnType,
            subject = safeVar,
            thenPart = irNull(),
            elsePart = safeVar
                .dotCall(irCall)
                .withValueArgs(*valueArgs.toTypedArray())
                .withTypeArgs(*typeArgs.toTypedArray())
            ,
        )
    }
    return rt
}

fun IrExpression.nullSafeExtensionDotCall(
    irCall: IrCall,
    builder: DeclarationIrBuilder,
    valueArgs: List<IrExpression>,
    typeArgs: List<IrType> = emptyList(),
): IrExpression {
    val safeVar = this@nullSafeExtensionDotCall
    val rt = builder.irBlock {
        +irIfNull(
            type = safeVar.type,
            subject = safeVar,
            thenPart = irNull(),
            elsePart = safeVar
                .extensionDotCall(irCall)
                .withValueArgs(*valueArgs.toTypedArray())
                .withTypeArgs(*typeArgs.toTypedArray())
            ,
        )
    }
    return rt
}


fun IrExpression.dotCall(irCall: IrCall): IrCall {
    irCall.dispatchReceiver = this
    return irCall
}

fun IrExpression.dotCall(irCall: () -> IrCall): IrCall {
    return dotCall(irCall())
}


fun IrExpression.dotCall(irCall: IrFunctionAccessExpression): IrFunctionAccessExpression {
    irCall.dispatchReceiver = this
    return irCall
}

fun IrExpression.dotCall(irCall: () -> IrFunctionAccessExpression): IrFunctionAccessExpression {
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

fun IrExpression.extensionDotCall(irCall: () -> IrFunctionAccessExpression): IrFunctionAccessExpression {
    return this.extensionDotCall(irCall())
}

fun IrFunctionAccessExpression.withValueArgs(vararg valueArgs: IrExpression): IrFunctionAccessExpression {
    for ((index, arg) in valueArgs.withIndex()) {
        this.putValueArgument(index, arg)
    }
    return this
}

fun IrFunctionAccessExpression.withValueArgs(args: List<IrExpression>): IrFunctionAccessExpression {
    return this.withValueArgs(*args.toTypedArray())
}


