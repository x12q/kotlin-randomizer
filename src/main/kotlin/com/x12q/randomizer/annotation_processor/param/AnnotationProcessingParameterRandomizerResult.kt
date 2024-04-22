package com.x12q.randomizer.annotation_processor.param

import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import kotlin.reflect.KClass

data class AnnotationProcessingParameterRandomizerResult(
    val validRandomizers: List<KClass<out ParameterRandomizer<*>>>,
    val invalidRandomizers: List<InvalidParamRandomizerReason>
) {
    operator fun plus(another: AnnotationProcessingParameterRandomizerResult): AnnotationProcessingParameterRandomizerResult {
        return copy(
            validRandomizers = validRandomizers + another.validRandomizers,
            invalidRandomizers = invalidRandomizers + another.invalidRandomizers,
        )
    }
}
