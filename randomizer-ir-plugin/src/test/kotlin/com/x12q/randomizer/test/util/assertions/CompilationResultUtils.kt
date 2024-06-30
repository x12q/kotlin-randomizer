package com.x12q.randomizer.test.util.assertions

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.lang.reflect.InvocationTargetException
import kotlin.test.fail

/**
 * Simply search for main function in a compilation result, then run it.
 * main function must not be in any package.
 */
@OptIn(ExperimentalCompilerApi::class)
fun JvmCompilationResult.runMain(packageName:String? = null) {
    val result = this

    /**
     * package name = "[packageName]." or ""
     */
    val pk=packageName?.let {
        "$it."
    }?:""
    val mainClass = "${pk}MainKt"

    val kClazz = result.classLoader.loadClass(mainClass)
    val main = kClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
    try {
        main.invoke(null)
    } catch (t: InvocationTargetException) {
        throw t.cause!!
    }
}

