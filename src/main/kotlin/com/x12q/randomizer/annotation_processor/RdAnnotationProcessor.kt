package com.x12q.randomizer.annotation_processor

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.x12q.randomizer.annotation_processor.clazz.InvalidClassRandomizerReason
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
     * Extract valid class randomizers class from [randomizerClass] list.
     * A valid randomizer is one that can generate random instances of [targetClassData] type
     * TODO this function does not take into account child classes
     */
    fun getValidClassRandomizer(
        targetClassData: RDClassData,
        randomizerClass: KClass<out ClassRandomizer<*>>
    ): Result<KClass<out ClassRandomizer<*>>, InvalidClassRandomizerReason> {

        if (randomizerClass.isAbstract) {
            return Err(InvalidClassRandomizerReason.IsAbstract(randomizerClass))
        } else {
            val classRandomizerType = randomizerClass
                .allSupertypes
                .firstOrNull { it.classifier == ClassRandomizer::class }

            if (classRandomizerType != null) {
                if (canProduceAssignableGeneric(classRandomizerType, targetClassData.kClass)) {
                    return Ok(randomizerClass)
                } else {
                    return Err(
                        InvalidClassRandomizerReason.UnableToGenerateTargetType(
                            rmdClass = randomizerClass,
                            actualClass = classRandomizerType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                            targetClass = targetClassData.kClass,
                        )
                    )
                }
            } else {
                return Err(InvalidClassRandomizerReason.IllegalClass(randomizerClass))
            }
        }
    }

    /**
     * Can produce instances assignable to [targetClass]
     */
    fun canProduceAssignableGeneric(randomizerType: KType, targetClass: KClass<*>): Boolean {
        val typesProducedByRandomizer = randomizerType.arguments.map {
            val variance = it.variance
            when (variance) {
                KVariance.INVARIANT, KVariance.OUT -> {
                    it.type?.classifier
                }

                else -> null
            }
        }

        return typesProducedByRandomizer.any { classifier ->
            (classifier as? KClass<*>)?.let {
                it == targetClass || it.isSubclassOf(targetClass)
            } ?: false
        }
    }

    /**
     * Extract valid parameter randomizer classes from [randomizerClass] list.
     * A valid parameter randomizer is one that can generate a random parameter described by [targetKParam] and belongs to a parent class (represented by [parentClassData])
     */
    fun getValidParamRandomizer(
        parentClassData: RDClassData,
        targetKParam: KParameter,
        randomizerClass: KClass<out ParameterRandomizer<*>>
    ): Result<KClass<out ParameterRandomizer<*>>, InvalidParamRandomizerReason> {

        when (val randomTargetKClassifier = targetKParam.type.classifier) {
            is KClass<*> -> {
                return getValidParamRandomizer(
                    parentClassData.kClass, targetKParam, randomTargetKClassifier, randomizerClass
                )
            }

            is KTypeParameter -> {
                return getValidParamRandomizer(
                    parentClassData, targetKParam, randomTargetKClassifier, randomizerClass
                )
            }

            else -> {

                return Err(
                    InvalidParamRandomizerReason.InvalidTarget(
                        randomizerClass = randomizerClass,
                        parentClass = parentClassData.kClass,
                        targetParam = targetKParam
                    )
                )
            }
        }
    }

    /**
     * Extract valid parameter randomizer classes from [randomizerClass] list.
     * This function is for when the parameter is of generic type.
     * TODO this function not completed. Because if the randomizer can generate a children type of the target class, then such randomizer is legal too
     */
    private fun getValidParamRandomizer(
        parentClassData: RDClassData,
        targetParam: KParameter,
        targetTypeParam: KTypeParameter,
        randomizerClass: KClass<out ParameterRandomizer<*>>
    ): Result<KClass<out ParameterRandomizer<*>>, InvalidParamRandomizerReason> {
        val targetClass = parentClassData.getKClassFor(targetTypeParam)
        if (targetClass != null) {
            if (randomizerClass.isAbstract) {
                return Err(
                    InvalidParamRandomizerReason.IsAbstract(
                        randomizerClass = randomizerClass,
                        targetParam = targetParam,
                        parentClass = parentClassData.kClass
                    )
                )
            } else {
                for (superType in randomizerClass.supertypes) {
                    if (superType.classifier == ParameterRandomizer::class) {
                        if (canProduceAssignableGeneric(superType, targetClass)) {
                            return Ok(randomizerClass)
                        } else {
                            return Err(
                                InvalidParamRandomizerReason.UnableToGenerateTarget(
                                    randomizerClass = randomizerClass,
                                    targetParam = targetParam,
                                    parentClass = parentClassData.kClass,
                                    actualClass = superType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                                    targetClass = targetClass,
                                )
                            )
                        }
                    } else {
                        return Err(
                            InvalidParamRandomizerReason.IllegalClass(
                                randomizerClass = randomizerClass,
                                targetParam = targetParam,
                                parentClass = parentClassData.kClass
                            )
                        )
                    }
                }
                return Err(
                    InvalidParamRandomizerReason.IllegalClass(
                        randomizerClass = randomizerClass,
                        targetParam = targetParam,
                        parentClass = parentClassData.kClass
                    )
                )
            }
        } else {
            throw IllegalArgumentException("$targetParam does not belong to $parentClassData")
        }
    }

    /**
     * Extract valid parameter randomizer classes from [randomizerClass] list.
     * This function is for when the parameter is of a concrete type.
     * TODO this function not completed. Because if the randomizer can generate a children type of the target class, then such randomizer is legal too
     */
    private fun getValidParamRandomizer(
        parentKClass: KClass<*>,
        targetKParam: KParameter,
        targetKClass: KClass<*>,
        randomizerClass: KClass<out ParameterRandomizer<*>>
    ): Result<KClass<out ParameterRandomizer<*>>, InvalidParamRandomizerReason> {


        if (randomizerClass.isAbstract) {
            return Err(
                InvalidParamRandomizerReason.IsAbstract(
                    randomizerClass = randomizerClass, targetParam = targetKParam, parentClass = parentKClass
                )
            )
        } else {
            val randomizerKType = randomizerClass.allSupertypes
                .firstOrNull { it.classifier == ParameterRandomizer::class }
            if (randomizerKType != null) {
                if (canProduceAssignableGeneric(randomizerKType, targetKClass)) {
                    return Ok(randomizerClass)
                } else {
                    return Err(
                        InvalidParamRandomizerReason.UnableToGenerateTarget(
                            randomizerClass = randomizerClass,
                            targetParam = targetKParam,
                            parentClass = parentKClass,
                            actualClass = randomizerKType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                            targetClass = targetKClass,
                        )
                    )
                }
            } else {
                return Err(
                    InvalidParamRandomizerReason.IllegalClass(
                        randomizerClass = randomizerClass,
                        targetParam = targetKParam,
                        parentClass = parentKClass
                    )
                )
            }
        }
    }

}

open class A<T>
open class A2 : A<Int>()
class A3 : A2()

fun main() {
    A3::class.allSupertypes.first {
        it.canProduceGeneric(Int::class)
    }.apply {
        println(this)
    }

}
