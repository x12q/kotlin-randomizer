package com.x12q.randomizer.test.util.assertions

import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

@OptIn(ExperimentalCompilerApi::class)
class GeneratedCodeAssertionBuilder(
    baseAssertions: GeneratedCodeAssertions = GeneratedCodeAssertions.empty(),
){
    var testCompilation: (KotlinCompilation.Result) -> Unit = baseAssertions.testCompilation
    var beforeVisitCall: (expression: IrCall) -> Unit = baseAssertions.beforeVisitCall
    var afterVisitCall: (input: IrCall, output: IrExpression) -> Unit = baseAssertions.afterVisitCall
    var beforeVisitClassNew: (IrClass) -> Unit = baseAssertions.beforeVisitClassNew
    var afterVisitClassNew: (input: IrClass, output: IrStatement) -> Unit = baseAssertions.afterVisitClassNew

    fun build(): GeneratedCodeAssertions {
        return GeneratedCodeAssertions(
            testCompilation = testCompilation,
            beforeVisitCall = beforeVisitCall,
            afterVisitCall = afterVisitCall,
            beforeVisitClassNew = beforeVisitClassNew,
            afterVisitClassNew = afterVisitClassNew
        )
    }
}
