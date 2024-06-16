package com.x12q.randomizer.ir_plugin.frontend.k2.util

import com.x12q.randomizer.ir_plugin.frontend.k2.base.BaseObjects
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol

/**
 * This file contains convenient functions for [FirClassSymbol] only
 */

fun FirClassSymbol<*>.getRandomizableAnnotation(session: FirSession): FirAnnotation? {

    val annotation = annotations.firstOrNull { annotation->
         annotation.fqName(session) == BaseObjects.randomizableFqName
    }
    val q = this.name.identifier.contains("Q123")
    if(q){
        println("zz")
    }
    return annotation
}

fun FirClassSymbol<*>.isAnnotatedRandomizable(session: FirSession): Boolean {
    return getRandomizableAnnotation(session) != null
}
