package com.x12q.randomizer.annotation_processor

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.x12q.randomizer.annotation_processor.clazz.InvalidClassRandomizerReason
import com.x12q.randomizer.annotation_processor.param.InvalidParamRandomizerReason
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.*
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.full.isSubclassOf


@Singleton
class RdAnnotationProcessor @Inject constructor() {

    /**
     * Check if a randomizer of class [randomizerClass] can generate instances of class described by [targetClass]
     */
    fun getValidClassRandomizer(
        targetClass: KClass<*>,
        randomizerClass: KClass<out ClassRandomizer<*>>
    ): Result<KClass<out ClassRandomizer<*>>, InvalidClassRandomizerReason> {

        if (randomizerClass.isAbstract) {
            return Err(InvalidClassRandomizerReason.IsAbstract(randomizerClass))
        } else {
            val classRandomizerType = randomizerClass
                .allSupertypes
                .firstOrNull { it.classifier == ClassRandomizer::class }

            if (classRandomizerType != null) {
                if (canProduceAssignable(classRandomizerType, targetClass)) {
                    return Ok(randomizerClass)
                } else {
                    return Err(
                        InvalidClassRandomizerReason.UnableToGenerateTargetType(
                            rmdClass = randomizerClass,
                            actualClass = classRandomizerType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                            targetClass = targetClass,
                        )
                    )
                }
            } else {
                return Err(InvalidClassRandomizerReason.IllegalClass(randomizerClass))
            }
        }
    }


    /**
     * Check if a randomizer of class [randomizerClass] can generate instances of class described by [targetClassData]
     */
    fun getValidClassRandomizer(
        targetClassData: RDClassData,
        randomizerClass: KClass<out ClassRandomizer<*>>
    ): Result<KClass<out ClassRandomizer<*>>, InvalidClassRandomizerReason> {
        return getValidClassRandomizer(targetClassData.kClass,randomizerClass)
    }

    /**
     * check if randomizer of [randomizerType] can produce an instance of [targetClass]
     */
    private fun canProduceAssignable(randomizerType: KType, targetClass: KClass<*>): Boolean {
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
     * Check if a randomizer of class [randomizerClass] can generate instances of parameter described by [targetParam] of parent class [parentClassData].
     */
    fun getValidParamRandomizer(
        parentClassData: RDClassData,
        targetParam: KParameter,
        randomizerClass: KClass<out ParameterRandomizer<*>>
    ): Result<KClass<out ParameterRandomizer<*>>, InvalidParamRandomizerReason> {

        when (val randomTargetKClassifier = targetParam.type.classifier) {
            is KClass<*> -> {
                return getValidParamRandomizer(
                    parentClassData.kClass, targetParam, randomTargetKClassifier, randomizerClass
                )
            }

            is KTypeParameter -> {
                return getValidParamRandomizer(
                    parentClassData, targetParam, randomTargetKClassifier, randomizerClass
                )
            }

            else -> {

                return Err(
                    InvalidParamRandomizerReason.InvalidTarget(
                        randomizerClass = randomizerClass,
                        parentClass = parentClassData.kClass,
                        targetParam = targetParam
                    )
                )
            }
        }
    }

    /**
     * Check if a randomizer of class [randomizerClass] can generate instances of parameter described by [targetParam] & [targetTypeParam] of parent class [parentClassData].
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

                val randomizerSuperType = randomizerClass.allSupertypes.firstOrNull {
                    it.classifier == ParameterRandomizer::class
                }

                if(randomizerSuperType!=null){
                    if (canProduceAssignable(randomizerSuperType, targetClass)) {
                        return Ok(randomizerClass)
                    } else {
                        return Err(
                            InvalidParamRandomizerReason.UnableToGenerateTarget(
                                randomizerClass = randomizerClass,
                                targetParam = targetParam,
                                parentClass = parentClassData.kClass,
                                actualClass = randomizerSuperType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                                targetClass = targetClass,
                            )
                        )
                    }
                }else{
                    return Err(
                        InvalidParamRandomizerReason.IllegalRandomizerClass(
                            randomizerClass = randomizerClass,
                            targetParam = targetParam,
                            parentClass = parentClassData.kClass
                        )
                    )
                }
            }
        } else {
            throw IllegalArgumentException("$targetParam does not belong to $parentClassData")
        }
    }

    /**
     * Check if a randomizer of class [randomizerClass] can generate instances of parameter described by [targetParam] & [targetClass] of parent class [parentClassData].
     */
    private fun getValidParamRandomizer(
        parentKClass: KClass<*>,
        targetParam: KParameter,
        targetClass: KClass<*>,
        randomizerClass: KClass<out ParameterRandomizer<*>>
    ): Result<KClass<out ParameterRandomizer<*>>, InvalidParamRandomizerReason> {


        if (randomizerClass.isAbstract) {
            return Err(
                InvalidParamRandomizerReason.IsAbstract(
                    randomizerClass = randomizerClass,
                    targetParam = targetParam,
                    parentClass = parentKClass
                )
            )
        } else {
            val randomizerKType = randomizerClass
                .allSupertypes
                .firstOrNull { it.classifier == ParameterRandomizer::class }

            if (randomizerKType != null) {
                if (canProduceAssignable(randomizerKType, targetClass)) {
                    return Ok(randomizerClass)
                } else {
                    return Err(
                        InvalidParamRandomizerReason.UnableToGenerateTarget(
                            randomizerClass = randomizerClass,
                            targetParam = targetParam,
                            parentClass = parentKClass,
                            actualClass = randomizerKType.arguments.firstOrNull()?.type?.classifier as KClass<*>,
                            targetClass = targetClass,
                        )
                    )
                }
            } else {
                return Err(
                    InvalidParamRandomizerReason.IllegalRandomizerClass(
                        randomizerClass = randomizerClass,
                        targetParam = targetParam,
                        parentClass = parentKClass
                    )
                )
            }
        }
    }
}
