package ab

import com.google.auto.service.AutoService
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.x12q.randomizer.ir_plugin.RandomizerModifierCommandLineProcessor
import com.x12q.randomizer.ir_plugin.transformers.di.DaggerP7Component
import com.x12q.randomizer.ir_plugin.transformers.randomizable.RandomizableTransformer
import com.x__q.randomizer_ir_plugin.BuildConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test


import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.FqName


class TestTransformer(
    val another: RandomizableTransformer,
    val testIt: (expression: IrFunctionAccessExpression) -> Unit
) : IrElementTransformerVoidWithContext() {
    private val randomFunctionName = FqName("com.x12q.randomizer.sample_app.makeRandomInstance")
    override fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrExpression {
        val rt = another.visitFunctionAccess(expression)
        if (expression.symbol.owner.fqNameWhenAvailable == randomFunctionName) {
            testIt(expression)
        }
        return rt
    }
}

class RandomizerModifierIrGenerationExtension2(
    val testIt: (expression: IrFunctionAccessExpression) -> Unit
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val comp = DaggerP7Component
            .builder()
            .setIRPluginContext(pluginContext)
            .build()
        val randomizableTransformer2 = comp.randomizableTransformer()

        val testTransformer = TestTransformer(randomizableTransformer2, testIt)
        moduleFragment.transform(testTransformer, null)
    }
}

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class RandomizerModifierComponentRegistrar2(
    val testIt: (expression: IrFunctionAccessExpression) -> Unit
) : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(
            extension = RandomizerModifierIrGenerationExtension2(testIt)
        )
    }
}
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class RandomizerModifierCommandLineProcessor2 : CommandLineProcessor {

    override val pluginId: String = BuildConfig.IR_PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = enableOption,
            valueDescription = "boolean",
            description = "enable or disable the ir-plugin",
            required = false,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        return when (option.optionName) {
            enableOption -> configuration.put(argEnable, value.toBooleanStrictOrNull() ?: true)
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }

    companion object {
        val enableOption = "enable"
        val argEnable = CompilerConfigurationKey<Boolean>(enableOption)
    }
}

class TestBench {
    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun qwe() {


        val ktFile = SourceFile.kotlin(
            name = "file1.kt",
            contents = """
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
            """.trimIndent()
        )

        val r = KotlinCompilation().apply {
            sources = listOf(ktFile)
            compilerPluginRegistrars = listOf(
                RandomizerModifierComponentRegistrar2(){
//                    println(it.dump())
                    println("x12: ${it.valueArgumentsCount}")
                    it.valueArguments.firstOrNull() shouldNotBe null
                }
            )
            commandLineProcessors = listOf(
                RandomizerModifierCommandLineProcessor2()
            )
            messageOutputStream = System.out
        }.compile()

        r.exitCode shouldBe KotlinCompilation.ExitCode.OK


    }
}
