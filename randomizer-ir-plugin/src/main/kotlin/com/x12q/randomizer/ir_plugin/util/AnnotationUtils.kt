package com.x12q.randomizer.ir_plugin.util

import com.x12q.randomizer.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.descriptors.annotations.Annotations

/**
 * Check if [Annotations] contains [Randomizable] annotation or not
 */
fun Annotations.isRandomizable():Boolean{
    return this.hasAnnotation(BaseObjects.randomizableFqName)
}








