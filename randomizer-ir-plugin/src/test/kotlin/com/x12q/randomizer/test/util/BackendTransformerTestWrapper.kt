package com.x12q.randomizer.test.util

import com.x12q.randomizer.test.util.assertions.GeneratedCodeAssertions
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression


/**
 * A wrapper around [candidate] that allow asserting the input and output of visit*() functions.
 */
class BackendTransformerTestWrapper(
    private val candidate: IrElementTransformerVoidWithContext,
    private val assertions: GeneratedCodeAssertions,
) : IrElementTransformerVoidWithContext() {

    override fun visitCall(expression: IrCall): IrExpression {
        assertions.beforeVisitCall(expression)
        val rt = super.visitCall(expression)
        assertions.afterVisitCall(expression,rt)
        return rt
    }

    override fun visitClassNew(declaration: IrClass): IrStatement {
        assertions.beforeVisitClassNew(declaration)
        val trans = candidate.visitClassNew(declaration)
        assertions.afterVisitClassNew(declaration, trans)
        return trans
    }

    override fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrExpression {
        val rt = candidate.visitFunctionAccess(expression)
        return rt
    }
}
