package com.x12q.randomizer

import com.github.michaelbull.result.*
import com.x12q.randomizer.Randomizable.Companion.getClassRandomizerOnlyRs
import com.x12q.randomizer.Randomizable.Companion.getClassRandomizerOrParamRandomizerRs
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.err.RandomizerErrors
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.RandomizerCollection
import com.x12q.randomizer.randomizer.config.DefaultRandomConfig
import com.x12q.randomizer.randomizer_checker.RandomizerChecker
import com.x12q.randomizer.util.ReflectionUtils
import com.x12q.randomizer.util.getEnumValue
import javax.inject.Inject
import kotlin.jvm.Throws
import kotlin.random.Random
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.primaryConstructor

/**
 * This class contains all the random generation logic.
 */
data class RandomGenerator @Inject constructor(
    private val random: Random,
    val lv1RandomizerCollection: RandomizerCollection,
    val randomizerChecker: RandomizerChecker,
    val defaultRandomConfig: DefaultRandomConfig,
) {

    fun random(classData: RDClassData): Any? {
        return random(
            classData = classData,
            lv2RandomizerClassLz = null,
        )
    }

    internal fun random(
        classData: RDClassData,
        lv2RandomizerClassLz: Lazy<ClassRandomizer<*>?>?,
    ): Any? {
        val targetClass: KClass<*> = classData.kClass
        val objectInstance = targetClass.objectInstance
        if (objectInstance != null) {
            return objectInstance
        } else {
            // lv1 = randomizer is provided explicitly by the users in the top-level random function
            // No need to check for return type because the lv1RandomizerCollection already covers that.
            val lv1Randomizer = lv1RandomizerCollection.getRandomizer(classData)

            val lv3RandomizerLz = lazy { getLv3Randomizer(targetClass) }

            return randomByLv(
                classData = classData,
                lv1Randomizer = lv1Randomizer,
                lv2RandomizerLz = lv2RandomizerClassLz,
                lv3RandomizerLz = lv3RandomizerLz,
            )
        }
    }

    fun randomInnerClass(classData: RDClassData, outerObj: Any?): Any? {
        return randomInnerClass(
            innerClassData = classData,
            enclosingObject = outerObj,
            lv2RandomizerClassLz = null,
        )
    }

    fun randomInnerClass(
        innerClassData: RDClassData,
        enclosingObject: Any?,
        lv2RandomizerClassLz: Lazy<ClassRandomizer<*>?>?,
    ): Any? {
        val targetClass: KClass<*> = innerClassData.kClass
        val objectInstance = targetClass.objectInstance
        if (objectInstance != null) {
            return objectInstance
        } else {
            // lv1 = randomizer is provided explicitly by the users in the top-level random function
            // No need to check for return type because the lv1RandomizerCollection already covers that.
            val lv1Randomizer = lv1RandomizerCollection.getRandomizer(innerClassData)

            val lv3RandomizerLz = lazy { getLv3Randomizer(targetClass) }

            return randomByLv(
                classData = innerClassData,
                outerObj = enclosingObject,
                lv1Randomizer = lv1Randomizer,
                lv2RandomizerLz = lv2RandomizerClassLz,
                lv3RandomizerLz = lv3RandomizerLz,
            )
        }
    }


    /**
     * This function create a random instance of some class represented by [classData].
     * To do that, this function goes through multiple randomizer options, from lv1 to lv4.
     * - lv1 randomizers are those provided directly by users via [lv1RandomizerCollection]
     * - lv2 randomizers are those provided by the annotation [Randomizable] at parameters in constructor
     * - lv3 randomizers are those provided by the annotation [Randomizable] at class
     * - lv4 is the default recursive randomizer baked into the logic of the function of this class.
     * Note:
     * There will not be any out type check on [lv2RandomizerLz] because at this point its out type was already erased.
     * So, [lv2RandomizerLz] must be sure that it can produce the expected type before being passed here.
     */
    private fun randomByLv(
        classData: RDClassData,
        /**
         * Enclosing object is used only in case [classData] is an inner class
         */
        outerObj: Any? = null,
        lv1Randomizer: ClassRandomizer<*>? = null,
        lv2RandomizerLz: Lazy<ClassRandomizer<*>?>? = null,
        lv3RandomizerLz: Lazy<ClassRandomizer<*>?>? = null
    ): Any? {

        val targetClass: KClass<*> = classData.kClass

        if (lv1Randomizer != null) {
            return lv1Randomizer.random()
        }

        val lv2Randomizer = lv2RandomizerLz?.value
        if (lv2Randomizer != null) {
            return lv2Randomizer.random()
        }

        val lv3RandomizerClass = lv3RandomizerLz?.value
        if (lv3RandomizerClass != null) {
            return lv3RandomizerClass.random()
        }

        val rdEnumAndPrim = randomEnumAndPrimitives(classData)
        if (rdEnumAndPrim != null) {
            return rdEnumAndPrim
        }

        // TODO simplify the block below
        if (targetClass.isAbstract) {

            throw IllegalArgumentException("can't randomized abstract class ${targetClass.qualifiedName}. The only way to generate random instances of abstract class is either provide a randomizer via @${Randomizable::class.simpleName} or via the random function")

        } else if (targetClass.isSealed) {

            val sealedSubClassList = targetClass.sealedSubclasses
            val randomSubClass = sealedSubClassList.random()

            return random(
                RDClassData(
                    kClass = randomSubClass,
                    /**
                     * seal class children does not need KType
                     */
                    kType = null
                )
            )

        } else {

            val constructor = pickAnnotatedConstructorButIgnoreAnnotationContent(targetClass)

            if (constructor != null) {

                val visibility = constructor.visibility
                val visibilityIsValid = visibility != null
                        && visibility != KVisibility.PRIVATE
                        && visibility != KVisibility.INTERNAL

                if (!visibilityIsValid) {
                    println("WARNING: target constructor of ${targetClass.qualifiedName}$ should be public or protected")
                }

                try {
                    if (targetClass.isInner) {

                        val arguments = constructor.parameters.takeLast(constructor.parameters.size - 1).map { kParam ->
                            randomConstructorParameter(
                                kParam = kParam,
                                parentClassData = classData,
                            )
                        }.toTypedArray()

                        return constructor.call(outerObj, *arguments)

                    } else {
                        val arguments = constructor.parameters.map { kParam ->
                            randomConstructorParameter(
                                kParam = kParam,
                                parentClassData = classData,
                            )
                        }.toTypedArray()

                        return constructor.call(*arguments)
                    }

                } catch (e: Throwable) {
                    throw e
                }
            } else {
                throw IllegalArgumentException("A primary constructor is need to make a random instance of ${targetClass.qualifiedName}$.")
            }
        }
    }


    internal fun pickConstructor(targetClass: KClass<*>): PickConstructorResult? {

        val constructors: Collection<KFunction<Any>> = targetClass.constructors

        /**
         * A rich annotated constructor is one that annotated with [Randomizable] and with a valid randomizer
         */
        val withRichRandomizer: MutableList<PickConstructorResult> = mutableListOf()

        /**
         * A poor annotated constructor is one that annotated with [Randomizable] and without any randomizer
         */
        val withPoorRandomizer: MutableList<KFunction<Any>> = mutableListOf()

        var stillConsiderPoorAnnotatedConstructor = true

        for (con in constructors) {

            val annotationList = con.findAnnotations<Randomizable>()

            if (annotationList.isNotEmpty()) {
                for (annotation in annotationList) {

                    val rdmClass = annotation
                        .getClassRandomizerOnlyRs(targetClass)
                        .getOrElse { typeErr -> throw typeErr.toException() }

                    if (rdmClass != null) {
                        randomizerChecker.checkValidRandomizerClassOrThrow(rdmClass, targetClass)
                        stillConsiderPoorAnnotatedConstructor = false
                        withRichRandomizer.add(PickConstructorResult(con, rdmClass))
                    }
                }
                if (stillConsiderPoorAnnotatedConstructor) {
                    withPoorRandomizer.add(con)
                }
            }
        }

        if (withRichRandomizer.isNotEmpty()) {
            /**
             * Pick a random from the richly annotated constructor
             */
            return withRichRandomizer.random()
        }

        if (withPoorRandomizer.isNotEmpty()) {
            /**
             * Pick a random from the blank annotated constructors
             */
            return PickConstructorResult(
                withPoorRandomizer.random(), null
            )
        }

        /**
         * Use primary constructor if there are not any annotated constructor
         */
        return targetClass.primaryConstructor?.let {
            PickConstructorResult(it, null)
        }
    }

    /**
     * Pick a random constructor among constructors annotated with [Randomizable] in [targetClass].
     * If none is found, return the primary constructor.
     */
    private fun pickAnnotatedConstructorButIgnoreAnnotationContent(targetClass: KClass<*>): KFunction<Any>? {
        val annotatedConstructors = targetClass.constructors.filter {
            it.findAnnotations<Randomizable>().firstOrNull() != null
        }
        if (annotatedConstructors.isNotEmpty()) {
            /**
             * If there are multiple annotated constructor, pick a random one
             */
            val annotatedConstructor = annotatedConstructors.random(random)

            return annotatedConstructor
        } else {
            return targetClass.primaryConstructor
        }
    }


    /**
     * Get lv3 randomizer from a class.
     * Throw an error if a randomizer exist, but of the wrong type.
     */
    @Throws(Throwable::class)
    internal fun getLv3Randomizer(targetClass: KClass<*>): ClassRandomizer<*>? {
        /**
         * First, extract the randomizer class in the [Randomizable] on the class
         */
        val classRdmRs = targetClass.findAnnotations(Randomizable::class)
            .firstOrNull()
            ?.getClassRandomizerOnlyRs(targetClass)

        val lv3ClassRandomizerClass = classRdmRs?.get()
        if (lv3ClassRandomizerClass != null) {
            val lv3Rdm = randomizerChecker.checkValidRandomizerClassRs(lv3ClassRandomizerClass, targetClass)
                .map {
                    ReflectionUtils.createClassRandomizer(lv3ClassRandomizerClass)
                }.getOrElse { typeCheckErr ->
                    throw typeCheckErr.toException()
                }
            return lv3Rdm
        } else {

            val constructorRs2 = pickConstructor(targetClass)
            val lv3 = constructorRs2?.randomizer
            if (lv3 != null) {
                return ReflectionUtils.createClassRandomizer(lv3)
            }
            return null
        }
    }

    @Throws(Throwable::class)
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

    /**
     * TODO this function is too large, break it apart and test each part
     */
    @Throws(Throwable::class)
    fun randomConstructorParameterRs(
        param: KParameter,
        enclosingClassData: RDClassData
    ): Result<Any?, ErrorReport> {

        /**
         * There are 2 types of parameter:
         * - clear-type parameter
         * - generic-type parameter
         *
         * both can be turned into clear-type, that means, both can be checked by clear-type checker
         */

        val paramType: KType = param.type

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
                        val i = rd.random(
                            parameterClassData = paramData,
                            parameter = param,
                            enclosingClassData = enclosingClassData
                        )
                        if (i != null) {
                            return Ok(i)
                        }
                    }
                }

                val lv2Lz = lazy {
                    val lv2paramClassOrParamRandomizer: Pair<KClass<out ClassRandomizer<*>>?, KClass<out ParameterRandomizer<*>>?>? = param
                        .findAnnotations(Randomizable::class).firstOrNull()
                        ?.getClassRandomizerOrParamRandomizerRs()
                        ?.getOrElse { err ->
                            throw err.toException()
                        }

                    // At this point, lv1 randomizer cannot be used, so move on to check lv2 and below randomizers
                    val lv2ClassRandomizer0 = lv2paramClassOrParamRandomizer?.first?.let { lv2Rd ->
                        randomizerChecker.checkValidRandomizerClassOrThrow(lv2Rd, classifier)
                        ReflectionUtils.createClassRandomizer(lv2Rd)
                    }

                    val lv2ParamRandomizer0 = lv2paramClassOrParamRandomizer?.second?.let { lv2Rd ->
                        randomizerChecker.checkValidRandomizerClassOrThrow(lv2Rd, classifier)
                        ReflectionUtils.createParamRandomizer(lv2Rd)
                    }

                    val lv2ParamRandomizer = lv2ParamRandomizer0?.let {
                        object : ClassRandomizer<Any?> {
                            override val returnedInstanceData: RDClassData = paramData

                            override fun isApplicableTo(classData: RDClassData): Boolean {
                                return classData == returnedInstanceData
                            }

                            override fun random(): Any? {
                                return lv2ParamRandomizer0.random(
                                    parameterClassData = paramData,
                                    parameter = param,
                                    enclosingClassData = enclosingClassData,
                                )
                            }
                        }
                    }

                    val lv2ClassRandomizer = lv2ParamRandomizer ?: lv2ClassRandomizer0
                    lv2ClassRandomizer
                }
                val rt = random(
                    classData = paramData,
                    lv2RandomizerClassLz = lv2Lz,
                )
                return Ok(rt)
            }
            /**
             * This is for generic-type parameters
             * such as: class Q<T>(val s:T)
             */
            is KTypeParameter -> {
                val parameterData = enclosingClassData.getDataFor(classifier)
                if (parameterData != null) {

                    // lv2 is extracted + type check here, then passed to random(). Within random(), it will be decided lv2 will be used or not.

                    val lv2Lz = lazy {
                        val lv2paramClassOrParamRandomizer = param
                            .findAnnotations(Randomizable::class).firstOrNull()
                            ?.getClassRandomizerOrParamRandomizerRs()
                            ?.getOrElse { err ->
                                throw err.toException()
                            }

                        val lv2ClassRandomizer0 = lv2paramClassOrParamRandomizer?.first?.let { lv2Rd ->
                            randomizerChecker.checkValidRandomizerClassRs(
                                randomizerClass = lv2Rd,
                                targetClass = parameterData.kClass
                            )
                            ReflectionUtils.createClassRandomizer(lv2Rd)
                        }

                        val lv2ParamRandomizer0 = lv2paramClassOrParamRandomizer?.second?.let { lv2Rd ->
                            randomizerChecker.checkValidParamRandomizer(
                                parentClassData = enclosingClassData,
                                targetParam = param,
                                targetTypeParam = classifier,
                                randomizerClass = lv2Rd
                            )
                            ReflectionUtils.createParamRandomizer(lv2Rd)
                        }

                        val lv2ParamRandomizer = lv2ParamRandomizer0?.let {
                            object : ClassRandomizer<Any?> {
                                override val returnedInstanceData: RDClassData = parameterData

                                override fun isApplicableTo(classData: RDClassData): Boolean {
                                    return classData == returnedInstanceData
                                }

                                override fun random(): Any? {
                                    return lv2ParamRandomizer0.random(
                                        parameterClassData = parameterData,
                                        parameter = param,
                                        enclosingClassData = enclosingClassData,
                                    )
                                }
                            }
                        }

                        val lv2ClassRandomizer = lv2ParamRandomizer ?: lv2ClassRandomizer0
                        lv2ClassRandomizer
                    }

                    return Ok(
                        random(
                            classData = parameterData,
                            lv2RandomizerClassLz = lv2Lz
                        )
                    )
                } else {
                    return Err(RandomizerErrors.TypeDoesNotExist.report(classifier, enclosingClassData))
                }
            }

            else -> return Err(RandomizerErrors.ClassifierNotSupported.report(classifier))
        }
    }

    private fun randomElement(paramKType: KType, parentClassData: RDClassData): Any? {

        when (val classifier = paramKType.classifier) {
            is KClass<*> -> {
                /**
                 * This is for normal parameter
                 */
                val paramClassData = RDClassData(classifier, paramKType)
                return random(paramClassData)
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
     * lv4 is the default randomizer, only used to generate random instances of primitive data types.
     */

    private fun randomEnumAndPrimitives(classData: RDClassData): Any? {
        return lv4EnumRandom(classData) ?: lv4RandomPrimitive(classData)
    }

    private fun lv4EnumRandom(classData: RDClassData): Any? {
        return getEnumValue(classData.kClass)?.random(random)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun lv4RandomPrimitive(classData: RDClassData): Any? {
        val clzz: KClass<*> = classData.kClass
        val rt = when (clzz) {
            Char::class -> randomChar()
            Int::class -> random.nextInt()
            Long::class -> random.nextLong()
            Float::class -> random.nextFloat()
            Double::class -> random.nextDouble()
            String::class -> randomString(random)
            Boolean::class -> random.nextBoolean()
            Byte::class -> random.nextBytes(1)[0]
            Short::class -> random.nextInt().toShort()
            List::class, Collection::class -> makeRandomList(classData)
            Map::class -> makeRandomMap(classData)
            Set::class -> makeRandomList(classData).toSet()
            else -> null
        }
        return rt
    }

    private val collectionSize: IntRange = defaultRandomConfig.collectionSize
    private val strSize: IntRange = defaultRandomConfig.stringSize

    private fun makeRandomList(classData: RDClassData): List<Any?> {
        val type: KType? = classData.kType
        if (type != null) {
            val numOfElements = random.nextInt(collectionSize.start, collectionSize.endInclusive + 1)
            val elemType = type.arguments[0].type!!
            return (1..numOfElements).map { randomElement(elemType, classData) }
        } else {
            throw IllegalArgumentException("Unable to get Ktype, therefore can't to generate random List")
        }
    }

    private fun makeRandomMap(classData: RDClassData): Map<Any?, Any?> {
        val type: KType? = classData.kType
        if (type != null) {
            val numOfElements = random.nextInt(collectionSize.start, collectionSize.endInclusive + 1)
            val keyType = type.arguments[0].type!!
            val valType = type.arguments[1].type!!
            val keys = (1..numOfElements).map { randomElement(keyType, classData) }
            val values = (1..numOfElements).map { randomElement(valType, classData) }
            return keys.zip(values).toMap()
        } else {
            throw IllegalArgumentException("Unable to get Ktype, therefore can't to generate random Map")
        }
    }

    private val charRange = ('A'..'z')

    private fun randomChar(): Char {
        return charRange.random(random)
    }

    private fun randomString(random: Random): String {
        return (1..random.nextInt(
            strSize.start,
            strSize.endInclusive + 1
        )).map { randomChar() }.joinToString(separator = "") { "$it" }
    }
}
