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

    fun makeRandomInstance(classData: ClassData): Any? {
        val classRef: KClass<*> = classData.kClass
        val type: KType = classData.kType
        val primitive = makeStandardInstanceOrNull(classRef, type)
        if (primitive != null) {
            return primitive
        }

        val constructors = classRef.constructors.shuffled(random)

        for (constructor in constructors) {
            try {
                val arguments = constructor.parameters
                    .map { makeRandomInstanceForParam(it.type, classRef, type) }
                    .toTypedArray()

                return constructor.call(*arguments)
            } catch (e: Throwable) {
                e.printStackTrace()
                // no-op. We catch any possible error here that might occur during class creation
            }
        }

        throw IllegalArgumentException()
    }

    private fun makeRandomInstanceForParam(paramType: KType, parentClassRef: KClass<*>, parentType: KType): Any? {
        when (val classifier = paramType.classifier) {
            is KClass<*> -> {
                /**
                 * This is for normal parameter
                 */
                return makeRandomInstance(ClassData(classifier, paramType))
            }
            /**
             * This is for cases when the param is of generic type
             * such as: class Q<T>(val s:T)
             */
            is KTypeParameter -> {
                val typeParameterName = classifier.name
                val typeParameterId = parentClassRef.typeParameters.indexOfFirst { it.name == typeParameterName }
                val parameterType = parentType.arguments[typeParameterId].type ?: typeOf<Any>()
                return makeRandomInstance(ClassData(parameterType.classifier as KClass<*>, parameterType))
            }
            else -> throw Error("Type of the classifier $classifier is not supported")
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun makeStandardInstanceOrNull(classRef: KClass<*>, type: KType) = when (classRef) {
        Any::class -> any
        Int::class -> random.nextInt()
        Long::class -> random.nextLong()
        Double::class -> random.nextDouble()
        Float::class -> random.nextFloat()
        Char::class -> makeRandomChar(random)
        String::class -> makeRandomString(random)
        List::class, Collection::class -> makeRandomList(classRef, type)
        Map::class -> makeRandomMap(classRef, type)
        else -> null
    }

    private fun makeRandomList(classRef: KClass<*>, type: KType): List<Any?> {
        val numOfElements = random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val elemType = type.arguments[0].type!!
        return (1..numOfElements)
            .map { makeRandomInstanceForParam(elemType, classRef, type) }
    }

    private fun makeRandomMap(classRef: KClass<*>, type: KType): Map<Any?, Any?> {
        val numOfElements = random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val keyType = type.arguments[0].type!!
        val valType = type.arguments[1].type!!
        val keys = (1..numOfElements)
            .map { makeRandomInstanceForParam(keyType, classRef, type) }
        val values = (1..numOfElements)
            .map { makeRandomInstanceForParam(valType, classRef, type) }
        return keys.zip(values).toMap()
    }

    private fun makeRandomChar(random: Random) = ('A'..'z').random(random)
    private fun makeRandomString(random: Random) =
        (1..random.nextInt(possibleStringSizes.start, possibleStringSizes.endInclusive + 1))
            .map { makeRandomChar(random) }
            .joinToString(separator = "") { "$it" }
}
