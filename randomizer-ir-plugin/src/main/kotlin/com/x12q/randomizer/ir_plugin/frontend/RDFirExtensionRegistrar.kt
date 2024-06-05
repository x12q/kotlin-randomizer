package com.x12q.randomizer.ir_plugin.frontend

import com.x12q.randomizer.ir_plugin.frontend.k2.RDFirGenerationExtension
import com.x12q.randomizer.ir_plugin.frontend.k2.diagnos.RDCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class RDFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::RDFirGenerationExtension
        +::RDCheckersExtension
    }
}
