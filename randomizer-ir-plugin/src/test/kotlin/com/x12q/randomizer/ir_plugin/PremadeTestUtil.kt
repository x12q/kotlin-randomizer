package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.backend.transformers.di.DaggerP7Component
import com.x12q.randomizer.ir_plugin.backend.transformers.randomizable.RDBackendTransformer
import com.x12q.randomizer.ir_plugin.frontend.k2.RDFirGenerationExtension
import com.x12q.randomizer.test.util.assertions.GeneratedCodeAssertionBuilder
import com.x12q.randomizer.test.util.testGeneratedCode
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import java.io.OutputStream
import java.util.*

/**
 * A shortcut to [testGeneratedCode] with pre-define all the relevant plugins + some default behaviors.
 */
@OptIn(ExperimentalCompilerApi::class)
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
        ::RDFirGenerationExtension,
    ),
    fileName: String = "kt_file_${UUID.randomUUID()}.kt",
    outputStream: OutputStream = System.out,
    configAssertionBuilder: GeneratedCodeAssertionBuilder.() -> Unit,
): KotlinCompilation.Result {
    val builder = GeneratedCodeAssertionBuilder()
    return testGeneratedCode(
        kotlinSource = kotlinSource,
        makeAssertions = {
            builder.configAssertionBuilder()
            builder.build()
        },
        makeBackendTransformer = backendTransformerFactory,
        frontEndTransformerFactoryFunctions = frontEndTransformerFactories,
        fileName = fileName,
        outputStream = outputStream,
    )
}
