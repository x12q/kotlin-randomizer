package com.x12q.randomizer.test.util

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.x12q.randomizer.ir_plugin.backend.transformers.di.DaggerP7Component
import com.x12q.randomizer.ir_plugin.frontend.RDFirExtensionRegistrar
import com.x12q.randomizer.ir_plugin.frontend.k2.base.BaseObjects
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.functions
import java.io.OutputStream
import kotlin.test.Test


class TestBench {


    private val f = MutableSharedFlow<IrClass?>()
    val cr = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun qwe() {
        runBlocking {
//            val j1 = launch {
//                f.collect {
//                    println(it)
//                    cr.cancel()
//                }
//            }
            testGeneratedCode(
                kotlinSource = """
                package com.x12q.randomizer.sample_app
                import com.x12q.randomizer.annotations.Randomizable
                fun main(){
                    println(Q123.random())
                }
                class Q123
                """.trimIndent(),
                    testCompilation = {
                        it.exitCode shouldBe KotlinCompilation.ExitCode.OK

                    },
            )
        }
    }

    @OptIn(ExperimentalCompilerApi::class)
    fun testGeneratedCode(
        @Language("kotlin")
        kotlinSource: String,
        fileName: String = "main.kt",
        outputStream: OutputStream = System.out,
        testCompilation: (KotlinCompilation.Result) -> Unit = {},
    ) {
        val ktFile = SourceFile.kotlin(
            name = fileName,
            contents = kotlinSource
        )


        val r = KotlinCompilation().apply {
            val irGenerationExtension = TestIRGenerationExtension(
                transformers = { pluginContext ->
                    val comp = DaggerP7Component
                        .builder()
                        .setIRPluginContext(pluginContext)
                        .build()

                    val transformer = comp.randomizableTransformer2()
                    val testTransformer = IRTransformerTester(
                        candidateTransformer = transformer,
                        testVisitClassNewAfter = { irClass, statement ->
                            if (irClass.name.toString().contains("Q123")) {
                                val companionObj = irClass.companionObject()
                                companionObj.shouldNotBeNull()
                                val randomFunction = companionObj.functions.firstOrNull {
                                    it.name.toString() == "random"
                                }
                                randomFunction.shouldNotBeNull()
                                randomFunction.origin shouldBe  IrDeclarationOrigin.GeneratedByPlugin(BaseObjects.randomizableDeclarationKey)
                                randomFunction.returnType shouldBe
                            }
                        }
                    )
                    listOf(testTransformer)
                }
            )
            sources = listOf(ktFile)
            compilerPluginRegistrars = listOf(
                TestComponentRegistrar(
                    irGenerationExtension = irGenerationExtension,
                    firGenerationExtension = RDFirExtensionRegistrar(),
                )
            )

            commandLineProcessors = listOf(DummyCommandLineProcessor())
            messageOutputStream = outputStream

            // this allows the subject code to access the dependencies of the project
            inheritClassPath = true

            // enable fir
            useK2 = true
        }.compile()
        testCompilation(r)
//        val kClazz = r.classLoader.loadClass("MainKt")
//        val main = kClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
//
//        try {
//            main.invoke(null)
//        } catch (t: InvocationTargetException) {
//            throw t.cause!!
//        }
//        fail("should have thrown assertion")

    }
}


