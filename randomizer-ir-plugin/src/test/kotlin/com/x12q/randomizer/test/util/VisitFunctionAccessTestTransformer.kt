package com.x12q.randomizer.test.util

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.FqName

class VisitFunctionAccessTestTransformer(
    private val randomizableTransformer: IrElementTransformerVoidWithContext,
    private val testBefore: (expression: IrExpression) -> Unit = {},
    private val testAfter: (expression: IrFunctionAccessExpression) -> Unit,
) : IrElementTransformerVoidWithContext() {

    private val randomFunctionName = FqName("com.x12q.randomizer.sample_app.makeRandomInstance")

    override fun visitCall(expression: IrCall): IrExpression {
        testBefore(expression)
        return super.visitCall(expression)
    }

    override fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrExpression {

        if (expression.symbol.owner.fqNameWhenAvailable == randomFunctionName) {
//            testBefore(expression)
        }

        val rt = randomizableTransformer.visitFunctionAccess(expression)

        if (expression.symbol.owner.fqNameWhenAvailable == randomFunctionName) {
            testAfter(expression)
        }
        return rt
    }

    override fun visitRawFunctionReference(expression: IrRawFunctionReference): IrExpression {
        return super.visitRawFunctionReference(expression)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression): IrExpression {
        return super.visitFunctionExpression(expression)
    }

    override fun visitFunctionReference(expression: IrFunctionReference): IrExpression {
        testBefore(expression)
        return super.visitFunctionReference(expression)
    }
}
