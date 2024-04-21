package com.x12q.randomizer

import com.github.michaelbull.result.*
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.err.RandomizerErrors
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.RandomizerCollection
import javax.inject.Inject
import kotlin.random.Random
import kotlin.reflect.*

data class Randomizer @Inject constructor(
    private val random: Random,
    val randomizerCollection: RandomizerCollection,
) {

    val possibleCollectionSizes: IntRange = 1..5
    val possibleStringSizes: IntRange = 1..10

    fun random(classData: RDClassData): Any? {
        val classRef: KClass<*> = classData.kClass
        val primitive = defaultPrimitiveRandomOrNull(classData)
        if (primitive != null) {
            return primitive
        }

        val customClassRdm = randomizerCollection.getRandomizer(classData)
        if(customClassRdm!=null){
            return customClassRdm.random()
        }else{
            val constructors = classRef.constructors.shuffled(random)

            for (constructor in constructors) {
                try {
                    val arguments = constructor.parameters.map { kParam ->
                        randomConstructorParameter(
                            kParam = kParam,
                            parentClassData = classData,
                        )
                    }.toTypedArray()
                    return constructor.call(*arguments)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    // no-op. We catch any possible error here that might occur during class creation
                }
            }
        }
        throw IllegalArgumentException()
    }

    fun randomConstructorParameter(kParam: KParameter, parentClassData: RDClassData): Any? {
        val rs = randomConstructorParameterRs(kParam, parentClassData)
        when (rs) {
            is Ok -> {
                return rs.value
            }
            is Err -> {
                val er = rs.error
                if (er.isType(RandomizerErrors.ClassifierNotSupported.header)) {
                    throw er.toException()
                } else {
                    return null
                }
            }
        }
    }

    fun randomConstructorParameterRs(
        kParam: KParameter,
        parentClassData: RDClassData
    ): Result<Any?, ErrorReport> {
        /**
         * There are 2 types of parameter:
         * - clear-type parameter
         * - generic-type parameter
         *
         * both can be turned into clear-type, that means, both can be checked by clear-type checker
         */

        val paramKType: KType = kParam.type
        when (val classifier = paramKType.classifier) {
            is KClass<*> -> {
                /**
                 * This is for normal parameter
                 */
                val paramData = RDClassData(classifier, paramKType)
                val paramRandomizer = randomizerCollection.getParamRandomizer(paramData)
                if(!paramRandomizer.isNullOrEmpty()){
                    for(rd in paramRandomizer){
                        if(rd.isApplicableTo(paramData,kParam,parentClassData)){
                            val rs = rd.randomRs(paramData,kParam,parentClassData)
                            return rs
                        }
                    }
                }

                val rt = random(paramData)
                return Ok(rt)
            }
            /**
             * This is for generic-type parameters
             * such as: class Q<T>(val s:T)
             */
            is KTypeParameter -> {
                val parameterData = parentClassData.getDataFor(classifier)
                if (parameterData != null) {
                    return Ok(random(parameterData))
                } else {
                    return Err(RandomizerErrors.TypeDoesNotExist.report(classifier, parentClassData))
                }
            }

            else -> return Err(RandomizerErrors.ClassifierNotSupported.report(classifier))
        }
    }

    private fun randomChildren(paramKType: KType, parentClassData: RDClassData): Any? {
        // this level does not contain param type, and it must not, because it is also used to generate non-parameter
        when (val classifier = paramKType.classifier) {
            is KClass<*> -> {
                /**
                 * This is for normal parameter
                 */
                val paramData = RDClassData(classifier, paramKType)
                return random(paramData)
            }
            /**
             * This is for generic-type parameters
             * such as: class Q<T>(val s:T)
             */
            is KTypeParameter -> {
                val parameterData = parentClassData.getDataFor(classifier)
                if (parameterData != null) {
                    return random(parameterData)
                } else {
                    throw IllegalArgumentException("type does not exist")
                }
            }

            else -> throw Error("Type of the classifier $classifier is not supported")
        }
    }


    /**
     * Default function to generate random primitive
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun defaultPrimitiveRandomOrNull(classData: RDClassData): Any? {
        val classRef: KClass<*> = classData.kClass
        val rt = when (classRef) {
            Short::class -> random.nextInt().toShort()
            Boolean::class -> random.nextBoolean()
            Int::class -> random.nextInt()
            Long::class -> random.nextLong()
            Double::class -> random.nextDouble()
            Float::class -> random.nextFloat()
            Char::class -> makeRandomChar(random)
            String::class -> makeRandomString(random)
            List::class, Collection::class -> makeRandomList(classData)
            Map::class -> makeRandomMap(classData)
            Set::class -> makeRandomList(classData).toSet()
            else -> null
        }
        return rt
    }

    private fun makeRandomList(classData: RDClassData): List<Any?> {
        val type: KType = classData.kType
        val numOfElements = random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val elemType = type.arguments[0].type!!
        return (1..numOfElements).map { randomChildren(elemType, classData) }
    }

    private fun makeRandomMap(classData: RDClassData): Map<Any?, Any?> {
        val type: KType = classData.kType
        val numOfElements = random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val keyType = type.arguments[0].type!!
        val valType = type.arguments[1].type!!
        val keys = (1..numOfElements).map { randomChildren(keyType, classData) }
        val values = (1..numOfElements).map { randomChildren(valType, classData) }
        return keys.zip(values).toMap()
    }

    private fun makeRandomChar(random: Random): Char {
        return ('A'..'z').random(random)
    }

    private fun makeRandomString(random: Random): String {
        return (1..random.nextInt(
            possibleStringSizes.start,
            possibleStringSizes.endInclusive + 1
        )).map { makeRandomChar(random) }.joinToString(separator = "") { "$it" }
    }
}
