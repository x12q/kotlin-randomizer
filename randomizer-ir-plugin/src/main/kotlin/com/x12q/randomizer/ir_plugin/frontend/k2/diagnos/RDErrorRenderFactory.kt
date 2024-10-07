package com.x12q.randomizer.ir_plugin.frontend.k2.diagnos

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory


/**
 * This map error to message, and register such message for rendering on IDE.
 * The registration is done in init{} block below
 */
object RDErrorRenderFactory : BaseDiagnosticRendererFactory() {

    init {
        RootDiagnosticRendererFactory.registerFactory(RDErrorRenderFactory)
    }

    override val MAP = KtDiagnosticFactoryToRendererMap("RandomizationFactoryRenderMap").apply {
        put(
            factory = RDErrors.errorTypeFactory,
            message = "Something {0}, Something {1}.",
            rendererA = CommonRenderers.STRING,
            rendererB = CommonRenderers.STRING,
        )

        put(
            RDErrors.warningTypeFactory,"some warning message"
        )
    }


}
