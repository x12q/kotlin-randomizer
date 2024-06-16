package com.x12q.randomizer.test.util

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

/**
 * A front end registrar for testing front end generation extension.
 */
class FrontEndTestExtensionRegistrar(
    private val transformerFactories: List<(FirSession) -> FirDeclarationGenerationExtension>
) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        transformerFactories.forEach {
            +it
        }
    }
}
