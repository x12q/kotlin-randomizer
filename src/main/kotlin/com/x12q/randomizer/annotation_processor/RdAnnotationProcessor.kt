package com.x12q.randomizer.annotation_processor

import com.x12q.randomizer.annotation_processor.clazz.AnnotationProcessingClassRandomizerResult
import com.x12q.randomizer.annotation_processor.clazz.InvalidClassRandomizerReason
import com.x12q.randomizer.annotation_processor.param.AnnotationProcessingParameterRandomizerResult
import com.x12q.randomizer.annotation_processor.param.InvalidParamRandomizerReason
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import com.x12q.randomizer.util.ReflectionUtils.containGeneric
import com.x12q.randomizer.util.ReflectionUtils.isAssignableToGenericOf
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.starProjectedType


@Singleton
class RdAnnotationProcessor @Inject constructor() {

    /**
     * TODO remember to raise warning if rt is empty, telling user that default randomizer will be used because none of the provided randomizer can be used.
     * Extract valid class randomizers class from [candidates] list.
     * A valid randomizer is one that can generate random instances of [RDClassData] type
     */
    fun getValidClassRandomizer(
        randomTarget: RDClassData,
        candidates: Array<KClass<out ClassRandomizer<*>>>
    ): AnnotationProcessingClassRandomizerResult {
        val randomTargetKClass = randomTarget.kClass
        return getValidClassRandomizer(
            randomTargetKClass, candidates
        )
    }
    /**
     * TODO remember to raise warning if rt is empty, telling user that default randomizer will be used because none of the provided randomizer can be used.
     * Extract valid class randomizers class from [candidates] list.
     * A valid randomizer is one that can generate random instances of [randomTargetKClass] type
     * TODO this function does not take into account child classes
     */
    fun getValidClassRandomizer(
        randomTargetKClass: KClass<*>,
        candidates: Array<KClass<out ClassRandomizer<*>>>
    ): AnnotationProcessingClassRandomizerResult {

        val validRdms = mutableListOf<KClass<out ClassRandomizer<*>>>()
        val invalidRdms: MutableList<InvalidClassRandomizerReason> = mutableListOf()

        for (randomizerClass in candidates) {
            if (randomizerClass.isAbstract) {
                invalidRdms.add(
                    InvalidClassRandomizerReason.IsAbstract(randomizerClass)
                )
            } else {
                if(randomTargetKClass.isAssignableToGenericOf(randomizerClass.starProjectedType)){
                    validRdms.add(randomizerClass)
//                    for (superKType in randomizerClass.supertypes) {
//                        if (superKType.classifier == ClassRandomizer::class) {
//                            if(randomTargetKClass.isAssignableToGenericOf(randomizerClass.starProjectedType)){
//                                validRdms.add(randomizerClass)
//                                break
//                            } else {
//                                invalidRdms.add(
//                                    InvalidClassRandomizerReason.WrongTargetType(
//                                        rmdClass = randomizerClass,
//                                        actualTypes = superKType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
//                                        expectedType = randomTargetKClass,
//                                    )
//                                )
//                                break
//                            }
//                        }
//                    }
                }
            }
        }

        val rt = AnnotationProcessingClassRandomizerResult(
            validRandomizers = validRdms,
            invalidRandomizers = invalidRdms
        )

        return rt
    }

    /**
     * Extract valid parameter randomizer classes from [candidates] list.
     * A valid parameter randomizer is one that can generate a random parameter described by [targetKParam] and belongs to a parent class (represented by [parentClassData])
     */
    fun getValidParamRandomizer(
        parentClassData: RDClassData,
        targetKParam: KParameter,
        candidates: Array<KClass<out ParameterRandomizer<*>>>
    ): AnnotationProcessingParameterRandomizerResult {

        when (val randomTargetKClassifier = targetKParam.type.classifier) {
            is KClass<*> -> {
                return getValidParamRandomizer(
                    parentClassData.kClass, targetKParam, randomTargetKClassifier, candidates
                )
            }

            is KTypeParameter -> {
                return getValidParamRandomizer(
                    parentClassData, targetKParam, randomTargetKClassifier, candidates
                )
            }

            else -> {
                val rt = AnnotationProcessingParameterRandomizerResult(
                    validRandomizers = emptyList(), invalidRandomizers = emptyList()
                )
                return rt
            }
        }

    }

    /**
     * Extract valid parameter randomizer classes from [candidates] list.
     * This function is for when the parameter is of generic type.
     * TODO this function not completed. Because if the randomizer can generate a children type of the target class, then such randomizer is legal too
     */
    private fun getValidParamRandomizer(
        parentClassData: RDClassData,
        targetKParam: KParameter,
        targetKTypeParam: KTypeParameter,
        candidates: Array<KClass<out ParameterRandomizer<*>>>
    ): AnnotationProcessingParameterRandomizerResult {

        val validRdms = mutableListOf<KClass<out ParameterRandomizer<*>>>()
        val invalidRdms: MutableList<InvalidParamRandomizerReason> = mutableListOf()

        val targetClass = parentClassData.getKClassFor(targetKTypeParam)
        if (targetClass != null) {
            for (paramRdm in candidates) {
                if (paramRdm.isAbstract) {
                    invalidRdms.add(
                        InvalidParamRandomizerReason.IsAbstract(
                            randomizerKClass = paramRdm,
                            targetKParam = targetKParam,
                            parentClass = parentClassData.kClass
                        )
                    )
                } else {
                    for (superKType in paramRdm.supertypes) {
                        if (superKType.classifier == ParameterRandomizer::class) {
                            if (superKType.containGeneric(targetClass)) {
                                validRdms.add(paramRdm)
                            } else {
                                invalidRdms.add(
                                    InvalidParamRandomizerReason.WrongTargetType(
                                        randomizerKClass = paramRdm,
                                        targetKParam = targetKParam,
                                        parentClass = parentClassData.kClass,
                                        actualTypes = superKType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                                        expectedType = targetClass,
                                    )
                                )
                                break
                            }
                        }
                    }
                }
            }
        } else {
            throw IllegalArgumentException("$targetKParam does not belong to $parentClassData")
        }

        val rt = AnnotationProcessingParameterRandomizerResult(
            validRandomizers = validRdms, invalidRandomizers = invalidRdms
        )

        return rt
    }

    /**
     * Extract valid parameter randomizer classes from [candidates] list.
     * This function is for when the parameter is of a concrete type.
     * TODO this function not completed. Because if the randomizer can generate a children type of the target class, then such randomizer is legal too
     */
    private fun getValidParamRandomizer(
        parentKClass: KClass<*>,
        targetKParam: KParameter,
        targetKClass: KClass<*>,
        candidates: Array<KClass<out ParameterRandomizer<*>>>
    ): AnnotationProcessingParameterRandomizerResult {

        val validRdms = mutableListOf<KClass<out ParameterRandomizer<*>>>()
        val invalidRdms: MutableList<InvalidParamRandomizerReason> = mutableListOf()


        for (paramRdm in candidates) {
            if (paramRdm.isAbstract) {
                invalidRdms.add(
                    InvalidParamRandomizerReason.IsAbstract(
                        randomizerKClass = paramRdm, targetKParam = targetKParam, parentClass = parentKClass
                    )
                )
            } else {
                for (superKType in paramRdm.supertypes) {
                    if (superKType.classifier == ParameterRandomizer::class) {
                        if (superKType.containGeneric(targetKClass)) {
                            validRdms.add(paramRdm)
                            break
                        } else {
                            invalidRdms.add(
                                InvalidParamRandomizerReason.WrongTargetType(
                                    randomizerKClass = paramRdm,
                                    targetKParam = targetKParam,
                                    parentClass = parentKClass,
                                    actualTypes = superKType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                                    expectedType = targetKClass,
                                )
                            )
                            break
                        }
                    }
                }
            }
        }

        val rt = AnnotationProcessingParameterRandomizerResult(
            validRandomizers = validRdms, invalidRandomizers = invalidRdms
        )

        return rt
    }

}
