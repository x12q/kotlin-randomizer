package com.x12q.randomizer.test.util

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.*

class IRTransformerTester(
    private val candidateTransformer: IrElementTransformerVoidWithContext,
    private val testBefore: (expression: IrExpression) -> Unit = {},
    private val testAfter: (expression: IrFunctionAccessExpression) -> Unit = {},
    private val testVisitClassNewBefore: (IrClass) -> Unit = {},
    private val testVisitClassNewAfter: (IrClass, IrStatement) -> Unit = {_,_->},
) : IrElementTransformerVoidWithContext() {


    override fun visitCall(expression: IrCall): IrExpression {
        testBefore(expression)
        return super.visitCall(expression)
    }

    override fun visitClassNew(declaration: IrClass): IrStatement {
        testVisitClassNewBefore(declaration)
        val trans = candidateTransformer.visitClassNew(declaration)
        testVisitClassNewAfter(declaration, trans)
        return trans
    }

    override fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrExpression {
        testBefore(expression)
        val rt = candidateTransformer.visitFunctionAccess(expression)
        testAfter(expression)
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
