package com.x12q.randomizer.ir_plugin.frontend.k2.diagnos

import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker

class RdExpressionChecker : ExpressionCheckers(){
    override val functionCallCheckers: Set<FirFunctionCallChecker>
        get() = super.functionCallCheckers
}
