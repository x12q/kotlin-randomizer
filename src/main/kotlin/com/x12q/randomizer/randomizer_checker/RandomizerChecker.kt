package com.x12q.randomizer.randomizer_checker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.orElse
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.randomizer.CommonRandomizer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.*
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.full.isSubclassOf


/**
 * Contains function to sort out randomizer for some given target class
 */
@Singleton
class RandomizerChecker @Inject constructor() {

    /**
     * Check if [randomizerClass] is applicable to [targetClass]. Throw if not. Do nothing otherwise.
     */
    @Throws(Exception::class)
    fun checkValidRandomizerClassOrThrow(
        randomizerClass: KClass<out CommonRandomizer<*>>,
        targetClass: KClass<*>,
    ){
        checkValidRandomizerClassRs(randomizerClass, targetClass).orElse { err->throw err.toException() }
    }
    /**
     * Check if a [randomizerClass] can produce instances of [targetClass]
     */
    fun checkValidRandomizerClassRs(
        randomizerClass: KClass<out CommonRandomizer<*>>,
        targetClass: KClass<*>,
    ): Result<KClass<out CommonRandomizer<*>>, ErrorReport> {

        if (randomizerClass.isAbstract) {
            return Err(InvalidRandomizerReason.IsAbstract.report(randomizerClass))
        } else {
            val classRandomizerType = randomizerClass
                .allSupertypes
                .firstOrNull { it.classifier == CommonRandomizer::class }

            if (classRandomizerType != null) {
                if (canProduce(classRandomizerType, targetClass)) {
                    return Ok(randomizerClass)
                } else {
                    return Err(
                        InvalidRandomizerReason.UnableToGenerateTargetType.report(randomizerClass,targetClass)
                    )
                }
            } else {
                return Err(InvalidRandomizerReason.InvalidRandomizerClass.report(randomizerClass))
            }
        }
    }

    /**
     * Check if a randomizer of class [randomizerClass] can generate instances of class described by [targetClass]
     */
    fun checkValidClassRandomizer(
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
                if (canProduce(classRandomizerType, targetClass)) {
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
    fun checkValidClassRandomizer(
        targetClassData: RDClassData,
        randomizerClass: KClass<out ClassRandomizer<*>>
    ): Result<KClass<out ClassRandomizer<*>>, InvalidClassRandomizerReason> {
        return checkValidClassRandomizer(targetClassData.kClass,randomizerClass)
    }

    /**
     * check if randomizer of [randomizerType] can produce an instance that can be assigned to [targetClass]
     */
    private fun canProduce(randomizerType: KType, targetClass: KClass<*>): Boolean {
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
    fun checkValidParamRandomizer(
        parentClassData: RDClassData,
        targetParam: KParameter,
        randomizerClass: KClass<out ParameterRandomizer<*>>
    ): Result<KClass<out ParameterRandomizer<*>>, InvalidParamRandomizerReason> {

        when (val randomTargetKClassifier = targetParam.type.classifier) {
            is KClass<*> -> {
                return checkValidParamRandomizer(
                    parentClassData.kClass, targetParam, randomTargetKClassifier, randomizerClass
                )
            }

            is KTypeParameter -> {
                return checkValidParamRandomizer(
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
     * Check if a randomizer of class [randomizerClass] can generate instances of parameter described by [targetParam] & [targetTypeParam] of parent class [enclosingClassData].
     */
    fun checkValidParamRandomizer(
        enclosingClassData: RDClassData,
        targetParam: KParameter,
        targetTypeParam: KTypeParameter,
        randomizerClass: KClass<out ParameterRandomizer<*>>
    ): Result<KClass<out ParameterRandomizer<*>>, InvalidParamRandomizerReason> {
        val targetClass = enclosingClassData.getKClassFor(targetTypeParam)
        if (targetClass != null) {
            if (randomizerClass.isAbstract) {
                return Err(
                    InvalidParamRandomizerReason.IsAbstract(
                        randomizerClass = randomizerClass,
                        targetParam = targetParam,
                        parentClass = enclosingClassData.kClass
                    )
                )
            } else {

                val randomizerSuperType = randomizerClass.allSupertypes.firstOrNull {
                    it.classifier == ParameterRandomizer::class
                }

                if(randomizerSuperType!=null){
                    if (canProduce(randomizerSuperType, targetClass)) {
                        return Ok(randomizerClass)
                    } else {
                        return Err(
                            InvalidParamRandomizerReason.UnableToGenerateTarget(
                                randomizerClass = randomizerClass,
                                targetParam = targetParam,
                                parentClass = enclosingClassData.kClass,
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
                            parentClass = enclosingClassData.kClass
                        )
                    )
                }
            }
        } else {
            throw IllegalArgumentException("$targetParam does not belong to $enclosingClassData")
        }
    }

    /**
     * Check if a randomizer of class [randomizerClass] can generate instances of parameter described by [targetParam] & [targetClass] of parent class [parentClassData].
     */
    private fun checkValidParamRandomizer(
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
                if (canProduce(randomizerKType, targetClass)) {
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
