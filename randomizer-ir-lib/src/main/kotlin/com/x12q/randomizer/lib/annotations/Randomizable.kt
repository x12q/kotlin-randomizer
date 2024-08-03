package com.x12q.randomizer.lib.annotations

import com.x12q.randomizer.lib.RandomConfig
import com.x12q.randomizer.lib.__DefaultRandomConfig
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass


/**
 * Annotation to provide custom randomizer to class, constructor
 */
@Target(CLASS, VALUE_PARAMETER, PROPERTY, CONSTRUCTOR)
@Retention(RUNTIME)
annotation class Randomizable(
    val randomConfig: KClass<out RandomConfig> = __DefaultRandomConfig::class,
)


