package com.x12q.randomizer.test.util.assertions

import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

@OptIn(ExperimentalCompilerApi::class)
class GeneratedCodeAssertions(
    val testCompilation: (KotlinCompilation.Result) -> Unit = {},
    val beforeVisitCall: (expression: IrCall) -> Unit = {},
    val afterVisitCall: (input: IrCall, output: IrExpression) -> Unit = { _, _->},
    val beforeVisitClassNew: (IrClass) -> Unit = {},
    val afterVisitClassNew: (input: IrClass, output: IrStatement) -> Unit = { _, _ -> },
){
    companion object{
        fun empty(): GeneratedCodeAssertions {
            return GeneratedCodeAssertions()
        }
    }
}
