package com.x12q.randomizer.ir_plugin

import com.google.auto.service.AutoService
import com.x12q.randomizer.ir_plugin.backend.RandomizerModifierIrGenerationExtension
import com.x12q.randomizer.ir_plugin.frontend.RDFirExtensionRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter


@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class RandomizerModifierComponentRegistrar() : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        val enable = configuration.get(RandomizerModifierCommandLineProcessor.argEnable,false)

        FirExtensionRegistrarAdapter.registerExtension(RDFirExtensionRegistrar())
        IrGenerationExtension.registerExtension(RandomizerModifierIrGenerationExtension(messageCollector,enable))
    }
}


