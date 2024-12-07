package com.x12q.randomizer.test.util.assertions

import com.tschuchort.compiletesting.JvmCompilationResult
import com.x12q.randomizer.test.util.TestOutput
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.PrintStream
import java.lang.reflect.InvocationTargetException

/**
 * Simply search for main function in a compilation result, then run it.
 */
@OptIn(ExperimentalCompilerApi::class)
fun JvmCompilationResult.runMain(packageName:String? = null, testOutputStream: TestOutputStream?=null) {
    val kClazz = findMainClass(packageName)
    val main = kClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }

    val oldStream = System.out

    if(testOutputStream!=null){
        val newPrintStream = PrintStream(testOutputStream.getByteArrayOutputStream())
        System.setOut(newPrintStream)
    }

    try {
        main.invoke(null)

        if(testOutputStream!=null){
            System.setOut(oldStream)
        }
    } catch (t: InvocationTargetException) {
        throw t.cause!!
    }
}

/**
 * Simply search for `runTest` function in MainKt, then run it.
 */
@OptIn(ExperimentalCompilerApi::class)
fun JvmCompilationResult.executeRunTestFunction(
    onTestOutput:(TestOutput)->Unit={}
):TestOutput {
    return this.executeRunTestFunction(null,onTestOutput)
}

/**
 * Simply search for `runTest` function in MainKt, then run it.
 */
@OptIn(ExperimentalCompilerApi::class)
fun JvmCompilationResult.executeRunTestFunction(
    /**
     * package name of the MainKt class.
     */
    packageName:String?,
    onTestOutput:(TestOutput)->Unit={}
):TestOutput {
    val kClazz = findMainClass(packageName)
    val runTestFunction = kClazz.declaredMethods.single { it.name == "runTest" && it.parameterCount == 0 }
    try {
        val rt = runTestFunction.invoke(null) as TestOutput
        onTestOutput(rt)
        return rt
    } catch (t: InvocationTargetException) {
        throw t.cause!!
    }
}

@OptIn(ExperimentalCompilerApi::class)
fun JvmCompilationResult.findMainClass(packageName:String? = null):Class<*> {
    val result = this
    /**
     * package name = "[packageName]." or ""
     */
    val pk=packageName?.let {
        "$it."
    }?:""
    val mainClassName = "${pk}MainKt"

    val mainClass = result.classLoader.loadClass(mainClassName)
    return mainClass
}
