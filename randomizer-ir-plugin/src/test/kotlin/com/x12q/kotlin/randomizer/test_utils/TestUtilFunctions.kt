package com.x12q.kotlin.randomizer.test_utils

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.x12q.kotlin.randomizer.ir_plugin.backend.RDBackendTransformer
import com.x12q.kotlin.randomizer.test_utils.assertions.GeneratedCodeAssertions
import com.x12q.kotlin.randomizer.test_utils.assertions.TestOutputStream
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
    makeAssertions:()-> GeneratedCodeAssertions,
    /**
     * name for the file that will contain [kotlinSource].
     * This is not a name for a real file, but the name for the virtual file that will be created by this test.
     * This name can be anything.
     */
    fileName: String,
    outputStream: OutputStream,
    testOutputStream: TestOutputStream,
) {

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
        messageOutputStream = ManyOutputStream(outputStream,testOutputStream)

        // this allows the subject code to access the dependencies of the project
        inheritClassPath = true
    }.compile()
    assertions.testCompilation(compileResult,testOutputStream)
    assertions.testOutputStream(testOutputStream)
}

