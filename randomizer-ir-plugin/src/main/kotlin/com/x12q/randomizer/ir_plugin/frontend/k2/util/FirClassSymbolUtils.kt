package com.x12q.randomizer.ir_plugin.frontend.k2.util

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol


fun FirClassSymbol<*>.getRandomizable(session: FirSession): FirAnnotation? {
    val annotation = annotations.firstOrNull { an->
         an.fqName(session)?.toString()?.contains("Randomizable") ?: false
    }
    return annotation
//    val rt = this.getAnnotationByClassId(BaseObjects.randomizableClassId, session)
//    return rt
}

fun FirClassSymbol<*>.isAnnotatedRandomizable(session: FirSession): Boolean {
    return getRandomizable(session) != null
}
