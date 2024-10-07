package com.x12q.randomizer.ir_plugin.frontend.k2

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension

/**
 * run at STATUS phase.
 * Allow changing status of declaration: visibility, modality, modifiers.
 * - visibilty: public, protected, private
 * - modality: open, final
 * - modifier: override
 */
class StatusTransformerExtension(session: FirSession): FirStatusTransformerExtension(session){
    override fun needTransformStatus(declaration: FirDeclaration): Boolean {
        TODO("return true on certain declaration to invoke this status transformer on such declaration")
    }
}
