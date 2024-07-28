package com.x12q.randomizer.ir_plugin.backend.transformers

import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression

/**
 * contain expression & variable declaration that is relevant to the creation of a [ClassRandomizerCollection]
 */
class RandomizerCollectionCodes(
    val getRandomCollectionExpr: IrExpression,
    private val randomizerBuilderVarDeclarationAndAssignment: IrVariable,
    private val runRandomizerBuilderConfig: IrExpression,
    private val randomizerCollectionVarDeclarationAndAssignment: IrVariable,
) {
    fun addCodeToBody(bodyBuilder: IrBlockBodyBuilder) {
        with(bodyBuilder) {
            +randomizerBuilderVarDeclarationAndAssignment
            +runRandomizerBuilderConfig
            +randomizerCollectionVarDeclarationAndAssignment
        }
    }
}
