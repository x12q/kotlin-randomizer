package com.x12q.randomizer.test.util.assertions

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import java.io.OutputStream

@OptIn(ExperimentalCompilerApi::class)
class GeneratedCodeAssertionBuilder(
    baseAssertions: GeneratedCodeAssertions = GeneratedCodeAssertions.empty(),
){
    var testCompilation: (JvmCompilationResult,TestOutputStream) -> Unit = baseAssertions.testCompilation
    var testOutputStream:(TestOutputStream) -> Unit = baseAssertions.testOutputStream
    var beforeVisitCall: (expression: IrCall, IrPluginContext) -> Unit = baseAssertions.beforeVisitCall
    var afterVisitCall: (input: IrCall, output: IrExpression, IrPluginContext) -> Unit = baseAssertions.afterVisitCall
    var beforeVisitClassNew: (IrClass, IrPluginContext) -> Unit = baseAssertions.beforeVisitClassNew
    var afterVisitClassNew: (input: IrClass, output: IrStatement, IrPluginContext) -> Unit = baseAssertions.afterVisitClassNew

    fun build(): GeneratedCodeAssertions {
        return GeneratedCodeAssertions(
            testCompilation = testCompilation,
            beforeVisitCall = beforeVisitCall,
            afterVisitCall = afterVisitCall,
            beforeVisitClassNew = beforeVisitClassNew,
            afterVisitClassNew = afterVisitClassNew,
            testOutputStream = testOutputStream,
        )
    }
}
