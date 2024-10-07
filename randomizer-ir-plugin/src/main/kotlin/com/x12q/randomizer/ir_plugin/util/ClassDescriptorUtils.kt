package com.x12q.randomizer.ir_plugin.util

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind

fun ClassDescriptor.isRandomizable():Boolean{
    return annotations.isRandomizable()
}

fun ClassDescriptor.isRandomizableClass():Boolean{
    return this.kind == ClassKind.CLASS && this.isRandomizable()
}

fun ClassDescriptor.isRandomizableInterface():Boolean{
    return this.kind == ClassKind.INTERFACE && this.isRandomizable()
}

fun ClassDescriptor.isRandomizableEnum():Boolean{
    return this.kind == ClassKind.ENUM_CLASS && this.isRandomizable()
}

fun ClassDescriptor.isRandomizableObj():Boolean{
    return this.kind == ClassKind.OBJECT && this.isRandomizable()
}
