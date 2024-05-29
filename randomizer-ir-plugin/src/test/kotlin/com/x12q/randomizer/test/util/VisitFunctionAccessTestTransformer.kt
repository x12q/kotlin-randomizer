package com.x12q.randomizer.test.util

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.FqName

class VisitFunctionAccessTestTransformer(
    private val randomizableTransformer: IrElementTransformerVoidWithContext,
    private val testBefore: (expression: IrFunctionAccessExpression) -> Unit = {},
    private val testAfter: (expression: IrFunctionAccessExpression) -> Unit,
) : IrElementTransformerVoidWithContext() {

    private val randomFunctionName = FqName("com.x12q.randomizer.sample_app.makeRandomInstance")

    override fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrExpression {
        if (expression.symbol.owner.fqNameWhenAvailable == randomFunctionName) {
            testBefore(expression)
        }

        val rt = randomizableTransformer.visitFunctionAccess(expression)

        if (expression.symbol.owner.fqNameWhenAvailable == randomFunctionName) {
            testAfter(expression)
        }
        return rt
    }
}
