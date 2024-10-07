package com.x12q.randomizer.ir_plugin.frontend.k2.diagnos

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.warning0

/**
 * This is the error will be used for reporting on [RDFirPluginClassChecker]
 */
object RDErrors{
    val errorTypeFactory by error2<PsiElement, String, String>()
    val warningTypeFactory by warning0<PsiElement>()
}
