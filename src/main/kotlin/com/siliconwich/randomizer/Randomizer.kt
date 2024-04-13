package com.siliconwich.randomizer

import kotlin.random.Random
import kotlin.reflect.*

class Randomizer0(
    private val random: Random,
) {

    val possibleCollectionSizes: IntRange = 1..5
    val possibleStringSizes: IntRange = 1..10
    val any: Any = "Anything"

    fun makeRandomInstance(classData: ClassData): Any {
        val primitive = makeRandomPrimitive(classData)
        if (primitive != null) {
            return primitive
        }
        val constructor = classData.findConstructorRs().component1()
        if (constructor != null) {
            val arguments = constructor.parameters
                .map { pr ->
                    makeRandomParameter(pr.type, classData)
                }
                .toTypedArray()
            val rt = constructor.call(*arguments)
            return rt
        } else {
            throw NoUsableConstructor()
        }
    }

    /**
     * Generate a random parameter for a class
     */
    private fun makeRandomParameter(paramType: KType, classData: ClassData): Any? {

        val classRef: KClass<*> = classData.kClass
        val type: KType = classData.kType

        return when (val classifier = paramType.classifier) {
            is KClass<*> -> makeRandomInstance(classData)
            is KTypeParameter -> {
                val typeParameterName = classifier.name
                val typeParameterId = classRef.typeParameters.indexOfFirst { it.name == typeParameterName }
                val parameterType = type.arguments[typeParameterId].type ?: typeOf<Any>()
                makeRandomInstance(
                    ClassData(
                        kClass = parameterType.classifier as KClass<*>,
                        kType = parameterType
                    )
                )
            }

            else -> throw Error("Type of the classifier $classifier is not supported")
        }
    }

    private fun makeRandomPrimitive(classData: ClassData): Any? {
        val classRef: KClass<*> = classData.kClass
        return when (classRef) {
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
    }

    private fun makeRandomList(classData: ClassData): List<Any?> {
        val type: KType = classData.kType
        val numOfElements =
            random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
        val elemType = type.arguments[0].type!!
        return (1..numOfElements)
            .map { makeRandomParameter(elemType, classData) }
    }

    private fun makeRandomMap(classData: ClassData): Map<Any?, Any?> {
        val type: KType = classData.kType
        val numOfElements =
            random.nextInt(possibleCollectionSizes.start, possibleCollectionSizes.endInclusive + 1)
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
