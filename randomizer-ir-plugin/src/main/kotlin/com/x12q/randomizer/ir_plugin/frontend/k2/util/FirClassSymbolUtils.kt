package com.x12q.randomizer.ir_plugin.frontend.k2.util

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol

/**
 * This file contains convenient functions for [FirClassSymbol] only
 */

/**
 * Check if a class is annotated with @Randomizable
 */
fun FirClassSymbol<*>.isAnnotatedRandomizable(session: FirSession): Boolean {
    return getRandomizableAnnotation(false, session) != null
}

/**
 * Extract @Randomizable annotation from a class
 */
fun FirClassSymbol<*>.getRandomizableAnnotation(needArguments: Boolean, session: FirSession): FirAnnotation? {

    val annotations = if (needArguments) {
        resolvedAnnotationsWithArguments
    } else {
        resolvedCompilerAnnotationsWithClassIds
    }
    val rt = annotations.getAnnotationByClassId(BaseObjects.randomizableClassId, session)

    return rt
}


