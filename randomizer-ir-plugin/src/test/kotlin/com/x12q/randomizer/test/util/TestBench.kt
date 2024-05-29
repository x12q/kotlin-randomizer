package com.x12q.randomizer.test.util

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.x12q.randomizer.ir_plugin.transformers.di.DaggerP7Component
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import java.io.OutputStream
import java.util.UUID
import kotlin.test.Test


class TestBench {
    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun qwe() {
        testVisitFunctionAccess(
            """
                package com.x12q.randomizer.sample_app
                fun main(){
                    ABC(1,"abc")
                    makeRandomInstance<ABC>()
                    someFunction()
                }
                
                data class ABC(
                    val numberx: Int,
                    val text123: String,
                )
                
                fun someFunction() {
                }
                
                fun <T> makeRandomInstance(f:(()->T)? = null):T?{
                    return f?.invoke()
                }
            """.trimIndent(),
            testCompilation = {
                it.exitCode shouldBe KotlinCompilation.ExitCode.OK
            },
            testAfter = { e ->
                e.valueArguments.firstOrNull() shouldNotBe null
            },
        )
    }

    fun testIrExpression(
        @Language("kotlin")
        kotlinSource: String,
        test:()->Unit,
    ){

    }

    @OptIn(ExperimentalCompilerApi::class)
    fun testVisitFunctionAccess(
        @Language("kotlin")
        kotlinSource: String,
        fileName: String = "file_${UUID.randomUUID()}.kt",
        msgOutStream: OutputStream = System.out,
        testCompilation: (JvmCompilationResult) -> Unit = {},
        testBefore: (IrFunctionAccessExpression) -> Unit = {},
        testAfter: (IrFunctionAccessExpression) -> Unit,
    ) {
        val ktFile = SourceFile.kotlin(
            name = fileName,
            contents = kotlinSource
        )

        val r = KotlinCompilation().apply {
            sources = listOf(ktFile)
            compilerPluginRegistrars = listOf(
                TestComponentRegistrar(
                    TestIrGenerationExtension(
                        transformers = { pluginContext ->
                            val comp = DaggerP7Component.builder()
                                .setIRPluginContext(pluginContext)
                                .build()

                            val transformer = comp.randomizableTransformer()
                            val testTransformer = VisitFunctionAccessTestTransformer(
                                randomizableTransformer = transformer,
                                testBefore = testBefore,
                                testAfter = testAfter,
                            )
                            listOf(testTransformer)
                        }
                    )
                )
            )
            commandLineProcessors = listOf(DummyCommandLineProcessor())
            messageOutputStream = msgOutStream
        }.compile()
        testCompilation(r)
    }
}

