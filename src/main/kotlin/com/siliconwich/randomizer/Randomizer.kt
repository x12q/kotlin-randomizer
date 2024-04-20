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

        throw IllegalArgumentException()
    }

    private fun randomConstructorParameter(kParam: KParameter, parentClassData: RDClassData): Any? {
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

                val randomizers = this.randomizerCollection.getCustomRandomizer(paramData)
                randomizers?.also { rl->
                    for(r in rl){
                        /**
                         * The problem is the randomizers itself, it must house the entire randomizing logic within itself in order to do the generation.
                         * If I connect the randomizer back to these random function, it will become a circle loop.
                         * Therefore, the randomizer must be terminal operations (like a factory function)
                         */
                        val randomInstances = r.random(paramData,kParam)
                    }
                }
                // randomizers can be used for check,

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
