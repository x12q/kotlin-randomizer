package com.x12q.randomizer.ir_plugin.frontend.k2.diagnos

import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker

class RdDeclarationChecker  : DeclarationCheckers() {
    override val classCheckers: Set<FirClassChecker> = setOf(RDFirPluginClassChecker)
}


