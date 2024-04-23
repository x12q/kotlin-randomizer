package com.x12q.randomizer.annotation_processor

import com.x12q.randomizer.annotation_processor.clazz.AnnotationProcessingClassRandomizerResult
import com.x12q.randomizer.annotation_processor.clazz.InvalidClassRandomizerReason
import com.x12q.randomizer.annotation_processor.param.AnnotationProcessingParameterRandomizerResult
import com.x12q.randomizer.annotation_processor.param.InvalidParamRandomizerReason
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import com.x12q.randomizer.util.ReflectionUtils.canProduceGeneric
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.*
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.full.isSubclassOf


@Singleton
class RdAnnotationProcessor @Inject constructor() {

    /**
     * TODO remember to raise warning if rt is empty, telling user that default randomizer will be used because none of the provided randomizer can be used.
     * Extract valid class randomizers class from [candidates] list.
     * A valid randomizer is one that can generate random instances of [targetClassData] type
     * TODO this function does not take into account child classes
     */
    fun getValidClassRandomizer(
        targetClassData: RDClassData,
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
                val classRandomizerType = randomizerClass
                    .allSupertypes
                    .firstOrNull { it.classifier == ClassRandomizer::class }

                if(classRandomizerType!=null){
                    if(canProduceAssignableGeneric(classRandomizerType,targetClassData.kClass)){
                        validRdms.add(randomizerClass)
                    }else{
                        invalidRdms.add(
                            InvalidClassRandomizerReason.WrongTargetType(
                                rmdClass = randomizerClass,
                                actualTypes = classRandomizerType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                                expectedType = targetClassData.kClass,
                            )
                        )
                    }
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
     * Can produce instances assignable to [targetClass]
     */
    fun canProduceAssignableGeneric(classRandomizerKType:KType, targetClass: KClass<*>):Boolean{
        val typesProducedByRandomizer =  classRandomizerKType.arguments.map {
            val variance = it.variance
            when(variance){
                KVariance.INVARIANT, KVariance.OUT ->{
                    it.type?.classifier
                }
                else -> null
            }
        }

        return typesProducedByRandomizer.any{classifier->
            (classifier as? KClass<*>)?.let{
                it == targetClass || it.isSubclassOf(targetClass)
            } ?: false
        }
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
                            if (canProduceAssignableGeneric(superKType,targetClass)) {
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
                val randomizerKType = paramRdm.allSupertypes
                    .firstOrNull { it.classifier == ParameterRandomizer::class }
                if(randomizerKType!=null){
                    if(canProduceAssignableGeneric(randomizerKType,targetKClass)){
                        validRdms.add(paramRdm)
                    }else{
                        invalidRdms.add(
                            InvalidParamRandomizerReason.WrongTargetType(
                                randomizerKClass = paramRdm,
                                targetKParam = targetKParam,
                                parentClass = parentKClass,
                                actualTypes = randomizerKType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                                expectedType = targetKClass,
                            )
                        )
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

open class A<T>
open class A2 : A<Int>()
class A3 : A2()

fun main() {
    A3::class.allSupertypes.first{
        it.canProduceGeneric(Int::class)
    }.apply{
        println(this)
    }

}
