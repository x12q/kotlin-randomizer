package com.x12q.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.isAnnotation
import org.jetbrains.kotlin.name.FqName
import kotlin.reflect.KClass


fun IrClass.isAnnotatedWith(annotationClazz: KClass<*>): Boolean {
    val rt = annotationClazz.qualifiedName?.let { annotationName ->
        this.annotations.any {
            it.isAnnotation(FqName(annotationName))
        }
    } ?: false
    return rt
}


fun IrClass.isAnnotatedWith(annotationName: FqName): Boolean {
    return this.getAnnotation(annotationName) != null
}


fun IrClass.isFinalOrOpenConcrete(): Boolean {
    return when (modality) {
        FINAL,
        OPEN -> true
        SEALED,
        ABSTRACT -> false
    }
}

fun IrClass.isAbstract():Boolean{
    return modality == ABSTRACT
}

fun IrClass.isSealed():Boolean{
    return modality == SEALED
}
