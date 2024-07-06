package com.x12q.randomizer.test.util

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.x12q.randomizer.ir_plugin.backend.transformers.RDBackendTransformer
import com.x12q.randomizer.test.util.assertions.GeneratedCodeAssertions
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import java.io.OutputStream

/**
 * Run [makeAssertions] against [kotlinSource] compiled by compiler that employ compiler plugins produced by [makeBackendTransformer] and [frontEndTransformerFactoryFunctions]
 */
@OptIn(ExperimentalCompilerApi::class)
fun testGeneratedCode(
    @Language("kotlin")
    kotlinSource: String,
    makeBackendTransformer: (IrPluginContext) -> RDBackendTransformer,
    frontEndTransformerFactoryFunctions: List<(FirSession) -> FirDeclarationGenerationExtension>,
    frontEndCheckerExtensionFactoryFunctions:List<(FirSession) -> FirAdditionalCheckersExtension>,
    makeAssertions:()->GeneratedCodeAssertions,
    fileName: String,
    outputStream: OutputStream = System.out,
): JvmCompilationResult {

    val ktFile = SourceFile.kotlin(
        name = fileName,
        contents = kotlinSource
    )

    val assertions = makeAssertions()

    val compileResult = KotlinCompilation().apply {
        sources = listOf(ktFile)
        compilerPluginRegistrars = listOf(
            TestComponentRegistrar(
                backendGenerationExtension = TestIRGenerationExtension(
                    makeTransformers = {irPluginContext->
                        listOf(
                            BackendTransformerTestWrapper(
                                candidate = makeBackendTransformer(irPluginContext),
                                assertions = assertions,
                            )
                        )
                    }
                ),
                frontEndExtensionRegistrar = FrontEndTestExtensionRegistrar(frontEndTransformerFactoryFunctions,frontEndCheckerExtensionFactoryFunctions),
            )
        )

        commandLineProcessors = listOf(DummyCommandLineProcessor())
        messageOutputStream = outputStream

        // this allows the subject code to access the dependencies of the project
        inheritClassPath = true

        // enable fir
//        useK2 = true
    }.compile()
    val testCompilation:(JvmCompilationResult) -> Unit = assertions.testCompilation
    testCompilation(compileResult)
    return compileResult
}

