package com.x12q.randomizer.test.util

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.x12q.randomizer.test.util.assertions.GeneratedCodeAssertions
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import java.io.OutputStream
import java.util.*

/**
 * Run [assertions] against [kotlinSource] compiled by compiler that employ compiler plugins produced by [backendTransformerFactory] and [frontEndTransformerFactories]
 */
@OptIn(ExperimentalCompilerApi::class)
fun testGeneratedCode(
    @Language("kotlin")
    kotlinSource: String,
    backendTransformerFactory: (IrPluginContext) -> IrElementTransformerVoidWithContext,
    frontEndTransformerFactories: List<(FirSession) -> FirDeclarationGenerationExtension>,
    assertions: GeneratedCodeAssertions,
    fileName: String,
    outputStream: OutputStream = System.out,
): KotlinCompilation.Result {

    val testCompilation: (KotlinCompilation.Result) -> Unit = assertions.testCompilation
    val ktFile = SourceFile.kotlin(
        name = fileName,
        contents = kotlinSource
    )

    val compileResult = KotlinCompilation().apply {
        sources = listOf(ktFile)
        compilerPluginRegistrars = listOf(
            TestComponentRegistrar(
                backendGenerationExtension = TestIRGenerationExtension(
                    transformers = { pluginContext ->
                        listOf(
                            BackendTransformerTestWrapper(
                                candidate = backendTransformerFactory(pluginContext),
                                assertions = assertions,
                            )
                        )
                    }
                ),
                frontEndExtensionRegistrar = FrontEndTestExtensionRegistrar(frontEndTransformerFactories),
            )
        )

        commandLineProcessors = listOf(DummyCommandLineProcessor())
        messageOutputStream = outputStream

        // this allows the subject code to access the dependencies of the project
        inheritClassPath = true

        // enable fir
        useK2 = true
    }.compile()
    testCompilation(compileResult)
    return compileResult
}

