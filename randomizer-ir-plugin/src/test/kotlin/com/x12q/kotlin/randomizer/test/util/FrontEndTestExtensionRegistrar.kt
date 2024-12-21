package com.x12q.kotlin.randomizer.test.util

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

/**
 * A front end registrar for **testing** front end generation extension.
 */
class FrontEndTestExtensionRegistrar(
    private val transformerFactoryFunctions: List<(FirSession) -> FirDeclarationGenerationExtension>,
    private val frontEndCheckerExtensionFactoryFunctions:List<(FirSession) -> FirAdditionalCheckersExtension>,
) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        transformerFactoryFunctions.forEach {
            +it
        }

        frontEndCheckerExtensionFactoryFunctions.forEach {
            +it
        }
    }
}
