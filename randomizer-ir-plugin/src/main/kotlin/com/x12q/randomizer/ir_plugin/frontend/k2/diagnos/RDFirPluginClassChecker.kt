package com.x12q.randomizer.ir_plugin.frontend.k2.diagnos

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.declarations.FirClass

class RDFirPluginClassChecker(mppKind: MppCheckerKind) : FirClassChecker(mppKind) {
    /**
     * Checker can check class, and report error/warning using [reporter].
     * This allows reporting errors to IDEs.
     */
    override fun check(declaration: FirClass, context: CheckerContext, reporter: DiagnosticReporter) {
//        reporter can report the error on various front, including source code, that will result in red highlighting in IDEs.
//        reporter.reportOn(
//            source = declaration.source,
//            factory = RDErrors.errorTypeFactory,
//            a = "abc",
//            b = "abc",
//            positioningStrategy = null
//        )
    }
}
