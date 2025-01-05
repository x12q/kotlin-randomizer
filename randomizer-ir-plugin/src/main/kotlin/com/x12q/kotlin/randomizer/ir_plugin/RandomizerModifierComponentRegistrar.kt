package com.x12q.kotlin.randomizer.ir_plugin

import com.google.auto.service.AutoService
import com.x12q.kotlin.randomizer.ir_plugin.backend.RDIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration


@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class RandomizerModifierComponentRegistrar() : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val enable = configuration.get(RandomizerModifierCommandLineProcessor.argEnable,false)
        IrGenerationExtension.registerExtension(RDIrGenerationExtension(enable))
    }
}


