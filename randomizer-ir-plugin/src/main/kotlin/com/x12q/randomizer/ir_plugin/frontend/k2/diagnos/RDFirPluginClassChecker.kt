package com.x12q.randomizer.ir_plugin.frontend.k2.diagnos

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.frontend.k2.util.getRandomizableAnnotation
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.getKClassArgument

object RDFirPluginClassChecker : FirClassChecker(MppCheckerKind.Common) {
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
//        val classSymbol = declaration.symbol
//        val annotation = classSymbol.getRandomizableAnnotation(true, context.session)
//        if(annotation!=null){
//            val randomConfig = annotation.getKClassArgument(BaseObjects.randomConfigParamName,context.session)
//            if(randomConfig!=null){
//                println(randomConfig)
//            }
//        }

    }
}
