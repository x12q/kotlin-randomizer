package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.JvmCompilationResult
import com.x12q.randomizer.ir_plugin.backend.transformers.di.DaggerP7Component
import com.x12q.randomizer.ir_plugin.backend.transformers.RDBackendTransformer
import com.x12q.randomizer.ir_plugin.frontend.k2.RDFrontEndGenerationExtension
import com.x12q.randomizer.ir_plugin.frontend.k2.diagnos.RDCheckersExtension
import com.x12q.randomizer.test.util.assertions.GeneratedCodeAssertionBuilder
import com.x12q.randomizer.test.util.assertions.StringTestOutputStream
import com.x12q.randomizer.test.util.assertions.TestOutputStream
import com.x12q.randomizer.test.util.testGeneratedCode
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.*

/**
 * A shortcut to [testGeneratedCode] with pre-define all the relevant plugins + some default behaviors.
 */
fun testGeneratedCodeUsingStandardPlugin(
    @Language("kotlin")
    kotlinSource: String,
    backendTransformerFactory: (IrPluginContext) -> RDBackendTransformer = { pluginContext ->
        val comp = DaggerP7Component
            .builder()
            .setIRPluginContext(pluginContext)
            .build()
        val transformer = comp.randomizableTransformer2()
        transformer
    },
    frontEndTransformerFactories: List<(FirSession) -> FirDeclarationGenerationExtension> = listOf(
        // ::RDFrontEndGenerationExtension,
    ),
    frontEndCheckerExtensionFactoryFunctions:List<(FirSession) -> FirAdditionalCheckersExtension> = listOf(
        ::RDCheckersExtension
    ),
    fileName: String = "main.kt",
    outputStream: OutputStream = System.out,
    testOutputStream:TestOutputStream = StringTestOutputStream(),
    configAssertionBuilder: GeneratedCodeAssertionBuilder.() -> Unit,
) {
    val builder = GeneratedCodeAssertionBuilder()
    testGeneratedCode(
        kotlinSource = kotlinSource.trimIndent(),
        makeAssertions = {
            builder.configAssertionBuilder()
            builder.build()
        },
        makeBackendTransformer = backendTransformerFactory,
        frontEndTransformerFactoryFunctions = frontEndTransformerFactories,
        frontEndCheckerExtensionFactoryFunctions =  frontEndCheckerExtensionFactoryFunctions,
        fileName = fileName,
        outputStream = outputStream,
        testOutputStream = testOutputStream,
    )
}
