package com.x12q.randomizer.annotation_processor.clazz

import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import kotlin.reflect.KClass

/**
 * Hold result for [ClassRandomizer] only
 */
data class AnnotationProcessingClassRandomizerResult(
    val validRandomizers: List<KClass<out ClassRandomizer<*>>>,
    val invalidRandomizers: List<InvalidClassRandomizerReason>
)
