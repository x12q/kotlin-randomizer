package com.siliconwich.randomizer

import com.siliconwich.randomizer.config.RandomizerConfig
import kotlin.random.Random
import kotlin.reflect.*

class Randomizer(
    private val random: Random,
    private val config: RandomizerConfig,
) {

    val possibleCollectionSizes: IntRange = 1..5
    val possibleStringSizes: IntRange = 1..10
    val any: Any = "Anything"

    fun random(classData: ClassData): Any? {
        val classRef: KClass<*> = classData.kClass
        val type: KType = classData.kType
        val primitive = makePrimitiveOrNull(classData)
        if (primitive != null) {
            return primitive
        }

        val constructors = classRef.constructors.shuffled(random)

        for (constructor in constructors) {
            try {
                val arguments = constructor.parameters
                    .map { makeRandomParameter(it.type, classData) }
                    .toTypedArray()

                return constructor.call(*arguments)
            } catch (e: Throwable) {
                e.printStackTrace()
                // no-op. We catch any possible error here that might occur during class creation
            }
        }

        throw IllegalArgumentException()
    }

    private fun makeRandomParameter(paramType: KType, parentClassData: ClassData): Any? {

        val parentClassRef: KClass<*> = parentClassData.kClass
        val parentType: KType = parentClassData.kType

        when (val classifier = paramType.classifier) {
            is KClass<*> -> {
                /**
                 * This is for normal parameter
                 */
                return random(ClassData(classifier, paramType))
            }
            /**
             * This is for cases when the param is of generic type
             * such as: class Q<T>(val s:T)
             */
            is KTypeParameter -> {
                val typeParameterName = classifier.name
                val typeParameterId = parentClassRef.typeParameters.indexOfFirst { it.name == typeParameterName }
                val parameterType = parentType.arguments[typeParameterId].type ?: typeOf<Any>()
                return random(ClassData(parameterType.classifier as KClass<*>, parameterType))
            }
            else -> throw Error("Type of the classifier $classifier is not supported")
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun makePrimitiveOrNull(classData: ClassData):Any? {
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

    private fun makeRandomList(classData: ClassData): List<Any?> {
        val type: KType = classData.kType
        val numOfElements = random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val elemType = type.arguments[0].type!!
        return (1..numOfElements)
            .map { makeRandomParameter(elemType, classData) }
    }

    private fun makeRandomMap(classData: ClassData): Map<Any?, Any?> {
        val type: KType = classData.kType
        val numOfElements = random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val keyType = type.arguments[0].type!!
        val valType = type.arguments[1].type!!
        val keys = (1..numOfElements)
            .map { makeRandomParameter(keyType, classData) }
        val values = (1..numOfElements)
            .map { makeRandomParameter(valType, classData) }
        return keys.zip(values).toMap()
    }

    private fun makeRandomChar(random: Random) = ('A'..'z').random(random)

    private fun makeRandomString(random: Random) =
        (1..random.nextInt(possibleStringSizes.start, possibleStringSizes.endInclusive + 1))
            .map { makeRandomChar(random) }
            .joinToString(separator = "") { "$it" }
}
