package com.x12q.kotlin.randomizer.lib.annotations

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass


/**
 * Annotation to provide custom randomizer to class, constructor
 */
@Deprecated("kept for reference only. Don't use.")
@Target(CLASS, VALUE_PARAMETER, PROPERTY, CONSTRUCTOR)
@Retention(RUNTIME)
annotation class Randomizable(
    val candidates: Array<KClass<*>> = [],
)


