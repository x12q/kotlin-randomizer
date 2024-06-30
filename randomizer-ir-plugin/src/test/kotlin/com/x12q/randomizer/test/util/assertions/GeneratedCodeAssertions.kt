package com.x12q.randomizer.test.util.assertions

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

@OptIn(ExperimentalCompilerApi::class)
class GeneratedCodeAssertions(
    val testCompilation: (JvmCompilationResult) -> Unit = {},
    val beforeVisitCall: (expression: IrCall, irPluginContext: IrPluginContext) -> Unit = { _, _ -> },
    val afterVisitCall: (input: IrCall, output: IrExpression, irPluginContext: IrPluginContext) -> Unit = { _, _, _ -> },
    val beforeVisitClassNew: (IrClass, irPluginContext: IrPluginContext) -> Unit = { _, _ -> },
    val afterVisitClassNew: (input: IrClass, output: IrStatement, irPluginContext: IrPluginContext) -> Unit = { _, _, _ -> },
) {
    companion object {
        fun empty(): GeneratedCodeAssertions {
            return GeneratedCodeAssertions()
        }
    }
}
