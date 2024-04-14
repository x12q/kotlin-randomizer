package com.siliconwich.randomizer

import com.siliconwich.randomizer.config.RandomizerCollection
import kotlin.random.Random
import kotlin.reflect.*

class Randomizer(
    private val random: Random,
    private val randomizerCollection: RandomizerCollection,
) {

    val possibleCollectionSizes: IntRange = 1..5
    val possibleStringSizes: IntRange = 1..10
    val any: Any = "Anything"

    fun random(classData: RDClassData): Any? {
        val classRef: KClass<*> = classData.kClass
        val primitive = makePrimitiveOrNull(classData)
        if (primitive != null) {
            return primitive
        }

        /**
         * There are 2 types of parameter:
         * - clear-type parameter
         * - generic-type parameter
         *
         * both can be turned into clear-type, that means, both can be checked by clear-type checker
         */

        val constructors = classRef.constructors.shuffled(random)

        for (constructor in constructors) {
            try {
                val arguments = constructor.parameters.map { param ->
                    randomConstructorParameter(
                        param = param,
                        parentClassData = classData,
                    )
                }.toTypedArray()
                return constructor.call(*arguments)
            } catch (e: Throwable) {
                e.printStackTrace()
                // no-op. We catch any possible error here that might occur during class creation
            }
        }

        throw IllegalArgumentException()
    }


    private fun randomConstructorParameter(param: KParameter, parentClassData: RDClassData): Any? {
        val paramKType: KType = param.type
        paramKType
        return randomParameter(paramKType, parentClassData)
    }

    private fun randomParameter(paramKType: KType, parentClassData: RDClassData): Any? {

        when (val classifier = paramKType.classifier) {
            is KClass<*> -> {
                // check here
                /**
                 * This is for normal parameter
                 */
                return random(RDClassData(classifier, paramKType))
            }
            /**
             * This is for generic-type parameters
             * such as: class Q<T>(val s:T)
             */
            is KTypeParameter -> {
                return makeRandomForKTypeParameter(classifier, parentClassData)
            }

            else -> throw Error("Type of the classifier $classifier is not supported")
        }
    }


    fun makeRandomForKTypeParameter(ktypeParam: KTypeParameter, parentClassData: RDClassData): Any? {
        val parameterData = parentClassData.getDataFor(ktypeParam)
        if (parameterData != null) {
            return random(parameterData)
        } else {
            throw IllegalArgumentException("type does not exist")
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun makePrimitiveOrNull(classData: RDClassData): Any? {
        val classRef: KClass<*> = classData.kClass
        val rt = when (classRef) {
            Any::class -> any
            Int::class -> random.nextInt()
            Long::class -> random.nextLong()
            Double::class -> random.nextDouble()
            Float::class -> random.nextFloat()
            Char::class -> makeRandomChar(random)
            String::class -> makeRandomString(random)
            List::class, Collection::class -> makeRandomList(classData)
            Map::class -> makeRandomMap(classData)
            else -> null
        }
        return rt
    }

    private fun makeRandomList(classData: RDClassData): List<Any?> {
        val type: KType = classData.kType
        val numOfElements = random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val elemType = type.arguments[0].type!!
        return (1..numOfElements).map { randomParameter(elemType, classData) }
    }

    private fun makeRandomMap(classData: RDClassData): Map<Any?, Any?> {
        val type: KType = classData.kType
        val numOfElements = random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val keyType = type.arguments[0].type!!
        val valType = type.arguments[1].type!!
        val keys = (1..numOfElements).map { randomParameter(keyType, classData) }
        val values = (1..numOfElements).map { randomParameter(valType, classData) }
        return keys.zip(values).toMap()
    }

    private fun makeRandomChar(random: Random) = ('A'..'z').random(random)

    private fun makeRandomString(random: Random) = (1..random.nextInt(
        possibleStringSizes.start,
        possibleStringSizes.endInclusive + 1
    )).map { makeRandomChar(random) }.joinToString(separator = "") { "$it" }
}
