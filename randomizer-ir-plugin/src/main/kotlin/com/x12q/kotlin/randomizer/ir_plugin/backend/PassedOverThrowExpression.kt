package com.x12q.kotlin.randomizer.ir_plugin.backend

import org.jetbrains.kotlin.ir.expressions.IrExpression

data class PassedOverThrowExpression(
    val throwExpress: IrExpression,
    val errMsg:String
)
