package com.x12q.kotlin.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression

fun IrVariable.withInit(expr:IrExpression):IrVariable{
    this.initializer = expr
    return this
}
