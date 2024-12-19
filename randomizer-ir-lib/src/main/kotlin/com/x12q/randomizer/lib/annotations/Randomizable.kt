package com.x12q.randomizer.lib.annotations

import com.x12q.randomizer.lib.RandomConfig
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass


/**
 * Annotation to provide custom randomizer to class, constructor
 */
@Deprecated("Don't use, this is kept for reference only. It serves no purpose.")
@Target(CLASS, VALUE_PARAMETER, PROPERTY, CONSTRUCTOR)
@Retention(RUNTIME)
annotation class Randomizable


