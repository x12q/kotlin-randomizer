package com.x12q.randomizer.ksp.util

import com.google.devtools.ksp.symbol.KSClassDeclaration

fun KSClassDeclaration.getPackageName():String?{
    val rt = this.qualifiedName?.getQualifier()
    return rt
}
