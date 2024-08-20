package com.x12q.randomizer.ir_plugin.frontend.k2

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef

/**
 * Called in 'SUPERTYPES' phase.
 * Allows adding additional super types to classes and interfaces.
 */
class SuperGenExtension(session:FirSession) : FirSupertypeGenerationExtension(session) {
    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService
    ): List<FirResolvedTypeRef> {
        TODO("Not yet implemented")
    }

    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean{
        TODO()
    }

}

