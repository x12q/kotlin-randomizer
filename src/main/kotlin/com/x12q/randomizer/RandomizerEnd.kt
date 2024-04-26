package com.x12q.randomizer

import com.github.michaelbull.result.*
import com.x12q.randomizer.Randomizable.Companion.getClassRandomizerOnly
import com.x12q.randomizer.Randomizable.Companion.getClassRandomizerOrParamRandomizer
import com.x12q.randomizer.randomizer_processor.RandomizerChecker
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.err.RandomizerErrors
import com.x12q.randomizer.randomizer.*
import javax.inject.Inject
import kotlin.random.Random
import kotlin.reflect.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations

data class RandomizerEnd @Inject constructor(
    private val random: Random,
    val lv1RandomizerCollection: RandomizerCollection,
    val randomizerChecker: RandomizerChecker
) {

    val possibleCollectionSizes: IntRange = 1..5
    val possibleStringSizes: IntRange = 1..10

    /**
     * There will not be any out type check on [lv2Randomizer] because at this point its out type was already erased.
     * So, I need to make sure that [lv2Randomizer] can produce the expected type before passing it here.
     */
    fun random(classData: RDClassData, lv2Randomizer: ClassRandomizer<*>? = null): Any? {
        val targetClass: KClass<*> = classData.kClass
        val primitive = defaultPrimitiveRandomOrNull(classData)
        if (primitive != null) {
            return primitive
        }
        val lv1Randomizer = lv1RandomizerCollection.getRandomizer(classData)
        if (lv1Randomizer != null) {
            // lv1 = randomizer is provided explicitly by the users in the top-level random function
            // No need to check for return type because the lv1RandomizerCollection already covers that.
            return lv1Randomizer.random()
        } else {

            if (lv2Randomizer != null) {
                // lv2 = this is randomizer annotated at param in constructor
                // There's not any out type checking here, so this randomizer must be pre-check
                return lv2Randomizer.random()
            } else {
                // lv 3 = randomizer in @Randomizer annotation
                val lv3ClassRandomizerClassRs = targetClass.findAnnotations(Randomizable::class)
                    .firstOrNull()
                    ?.getClassRandomizerOnly(targetClass)

                val lv3ClassRandomizerClass = lv3ClassRandomizerClassRs
                    ?.getOrElse { err ->
                        throw err.toException()
                    }

                if (lv3ClassRandomizerClass != null) {

                    val rt = randomizerChecker.checkValidRandomizerClassRs(lv3ClassRandomizerClass, targetClass)
                        .map {
                            lv3ClassRandomizerClass
                                .createInstance()
                                .random()
                        }.getOrElse {
                            throw it.toException()
                        }
                    return rt

                } else {
                    // lv 4 - default recursive randomizer
                    if (targetClass.isAbstract) {
                        // TODO add a better error handling + more meaningful msg
                        throw IllegalArgumentException("can't randomized abstract class, either provide a randomizer via @${Randomizable::class.simpleName} or via the random function")
                    } else {
                        val constructors = targetClass.constructors.shuffled(random)
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
        param: KParameter,
        parentClassData: RDClassData
    ): Result<Any?, ErrorReport> {

        /**
         * There are 2 types of parameter:
         * - clear-type parameter
         * - generic-type parameter
         *
         * both can be turned into clear-type, that means, both can be checked by clear-type checker
         */

        val paramType: KType = param.type

        // TODO move lv2 to after lv1
        val lv2paramClassOrParamRandomizer = param
            .findAnnotations(Randomizable::class).firstOrNull()
            ?.getClassRandomizerOrParamRandomizer()
            ?.getOrElse {
                throw it.toException()
            }

        val classifier = paramType.classifier
        when (classifier) {
            is KClass<*> -> {
                /**
                 * This is for normal parameter
                 */
                val paramData = RDClassData(classifier, paramType)
                val lv1Randomizer = lv1RandomizerCollection.getParamRandomizer(paramData)
                if (!lv1Randomizer.isNullOrEmpty()) {
                    for (rd in lv1Randomizer) {
                        if (rd.isApplicableTo(paramData, param, parentClassData)) {
                            val rs = rd.random(paramData, param, parentClassData)
                            return Ok(rs)
                        }
                    }
                }

                // At this point, not lv1 randomizer can be used, so move on to check lv2 randomizers
                val lv2ClassRandomizer0 = lv2paramClassOrParamRandomizer?.first?.let{lv2Rd->
                    randomizerChecker.checkValidRandomizerClassOrThrow(lv2Rd,classifier)
                    lv2Rd.createInstance()
                }

                val lv2ParamRandomizer0 = lv2paramClassOrParamRandomizer?.second?.let { lv2Rd->
                    randomizerChecker.checkValidRandomizerClassOrThrow(lv2Rd,classifier)
                    lv2Rd.createInstance()
                }

                val lv2ParamRandomizer = lv2ParamRandomizer0?.let {
                    object : ClassRandomizer<Any?> {
                        override val targetClassData: RDClassData = paramData

                        override fun isApplicable(classData: RDClassData): Boolean {
                            return classData == targetClassData
                        }

                        override fun random(): Any? {
                            return lv2ParamRandomizer0.random(
                                parameterClassData = paramData,
                                parameter = param,
                                parentClassData = parentClassData,
                            )
                        }
                    }
                }

                val lv2ClassRandomizer = lv2ParamRandomizer ?: lv2ClassRandomizer0
                val rt = random(
                    classData = paramData,
                    lv2Randomizer = lv2ClassRandomizer,
                )
                return Ok(rt)
            }
            /**
             * This is for generic-type parameters
             * such as: class Q<T>(val s:T)
             */
            is KTypeParameter -> {
                val parameterData = parentClassData.getDataFor(classifier)
                if (parameterData != null) {

                    val lv2ClassRandomizer0 = lv2paramClassOrParamRandomizer?.first?.let{lv2Rd->
                        randomizerChecker.checkValidRandomizerClassRs(
                            randomizerClass = lv2Rd,
                            targetClass = parameterData.kClass
                        )
                        lv2Rd.createInstance()
                    }

                    val lv2ParamRandomizer0 = lv2paramClassOrParamRandomizer?.second?.let { lv2Rd->
                        randomizerChecker.checkValidParamRandomizer(
                            parentClassData = parentClassData,
                            targetParam = param,
                            targetTypeParam = classifier,
                            randomizerClass=lv2Rd
                        )
                        lv2Rd.createInstance()
                    }

                    val lv2ParamRandomizer = lv2ParamRandomizer0?.let {
                        object : ClassRandomizer<Any?> {
                            override val targetClassData: RDClassData = parameterData

                            override fun isApplicable(classData: RDClassData): Boolean {
                                return classData == targetClassData
                            }

                            override fun random(): Any? {
                                return lv2ParamRandomizer0.random(
                                    parameterClassData = parameterData,
                                    parameter = param,
                                    parentClassData = parentClassData,
                                )
                            }
                        }
                    }

                    val lv2ClassRandomizer = lv2ParamRandomizer ?: lv2ClassRandomizer0


                    return Ok(random(
                        classData = parameterData,
                        lv2Randomizer = lv2ClassRandomizer,
                    ))
                } else {
                    return Err(RandomizerErrors.TypeDoesNotExist.report(classifier, parentClassData))
                }
            }

            else -> return Err(RandomizerErrors.ClassifierNotSupported.report(classifier))
        }
    }

    // TODO consider randomizer lv1,2,3,4 here
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
