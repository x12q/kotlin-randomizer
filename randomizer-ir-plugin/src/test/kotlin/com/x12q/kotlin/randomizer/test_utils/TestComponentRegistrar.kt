package com.x12q.kotlin.randomizer.test_utils

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class TestComponentRegistrar(
    val backendGenerationExtension:IrGenerationExtension,
    val frontEndExtensionRegistrar: FirExtensionRegistrar
) : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(backendGenerationExtension)
        FirExtensionRegistrarAdapter.registerExtension(frontEndExtensionRegistrar)
    }
}
