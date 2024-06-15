package com.x12q.randomizer.test.util

import com.x12q.randomizer.ir_plugin.backend.RDIrGenerationExtension
import com.x12q.randomizer.ir_plugin.frontend.RDFirExtensionRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class TestComponentRegistrar(
    val irGenerationExtension:IrGenerationExtension,
    val firGenerationExtension: FirExtensionRegistrar
) : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(irGenerationExtension)
        FirExtensionRegistrarAdapter.registerExtension(firGenerationExtension)
    }
}
