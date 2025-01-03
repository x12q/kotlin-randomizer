package com.x12q.kotlin.randomizer.ir_plugin.backend.utils

import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.name.FqName

fun IrConstructor.isAnnotatedWithRandomizable():Boolean{
    return this.isAnnotatedWith(BaseObjects.randomizableFqName)
}

fun IrConstructor.isAnnotatedWith(annotationName: FqName): Boolean {
    return this.getAnnotation(annotationName) != null
}

fun IrConstructor.isPublic(): Boolean{
    return this.visibility.name == Visibilities.Public.name
}

fun IrConstructor.isInternal(): Boolean{
    return this.visibility.name == Visibilities.Internal.name
}
