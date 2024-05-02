package com.x12q.randomizer

import com.github.michaelbull.result.*
import com.x12q.randomizer.Randomizable.Companion.getClassRandomizerOnly
import com.x12q.randomizer.Randomizable.Companion.getClassRandomizerOrParamRandomizer
import com.x12q.randomizer.randomizer_processor.RandomizerChecker
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.err.RandomizerErrors
import com.x12q.randomizer.randomizer.*
import com.x12q.randomizer.randomizer.ClassRandomizer
import javax.inject.Inject
import kotlin.random.Random
import kotlin.reflect.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.primaryConstructor

data class RandomizerEnd @Inject constructor(
    private val random: Random,
    val lv1RandomizerCollection: RandomizerCollection,
    val randomizerChecker: RandomizerChecker
) {

    /**
     * This function create a random instance of some class represented by [classData].
     * To do that, this function goes through multiple randomizer options, from lv1 to lv4.
     * - lv1 randomizers are those provided directly by users via [lv1RandomizerCollection]
     * - lv2 randomizers are those provided by the annotation [Randomizable] at constructor parameters
     * - lv3 randomizers are those provided by the annotation [Randomizable] at class
     * - lv4 is the default recursive randomizer baked into the logic of the function of this class.
     * Note:
     * There will not be any out type check on [lv2Randomizer] because at this point its out type was already erased.
     * So, I need to make sure that [lv2Randomizer] can produce the expected type before passing it here.
     */
    private fun randomByLv(
        classData: RDClassData,
        lv1Randomizer: ClassRandomizer<*>? = null,
        lv2RandomizerLz: Lazy<ClassRandomizer<*>?>? = null,
        lv3RandomizerLz: Lazy<ClassRandomizer<*>?>? = null
    ): Any? {
        // todo, remove this primitive randomizer, replace it with my randomizer

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

        val lv4RandomInstance = lv4Random(classData)
        if (lv4RandomInstance != null) {
            return lv4RandomInstance
        }

        val targetClass: KClass<*> = classData.kClass

        // TODO simplify the block below
        if (targetClass.isAbstract) {
            throw IllegalArgumentException("can't randomized abstract class ${targetClass.qualifiedName}. The only way to generate random instances of abstract class is either provide a randomizer via @${Randomizable::class.simpleName} or via the random function")
        } else {
            val primaryConstructor = targetClass.primaryConstructor
            if (primaryConstructor != null) {

                val visibility = primaryConstructor.visibility

                val visibilityIsValid =
                    visibility != null
                            && visibility != KVisibility.PRIVATE
                            && visibility != KVisibility.INTERNAL

                if (!visibilityIsValid) {
                    println("WARNING: primary constructor of ${targetClass.qualifiedName}$ should be public or protected")
                }

                try {
                    val arguments = primaryConstructor.parameters.map { kParam ->
                        randomConstructorParameter(
                            kParam = kParam,
                            parentClassData = classData,
                        )
                    }.toTypedArray()
                    return primaryConstructor.call(*arguments)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            } else {
                throw IllegalArgumentException("A primary constructor is need to make a random instance of ${targetClass.qualifiedName}$.")
            }
        }

        throw IllegalArgumentException()
    }

    fun random(classData: RDClassData):Any?{
        return random(classData, lv2RandomizerClassLz = null)
    }

    fun random(
        classData: RDClassData,
        lv2RandomizerClassLz: Lazy<ClassRandomizer<*>?>?,
    ):Any?{

        val targetClass: KClass<*> = classData.kClass

        // lv1 = randomizer is provided explicitly by the users in the top-level random function
        // No need to check for return type because the lv1RandomizerCollection already covers that.
        val lv1Randomizer = lv1RandomizerCollection.getRandomizer(classData)

        val lv3RandomizerLz = lazy {
            val lv3ClassRandomizerClass = targetClass.findAnnotations(Randomizable::class)
                .firstOrNull()
                ?.getClassRandomizerOnly(targetClass)
                ?.getOrElse { err -> throw err.toException() }
            val lv3Rdm = if(lv3ClassRandomizerClass!=null){
               randomizerChecker.checkValidRandomizerClassRs(lv3ClassRandomizerClass, targetClass)
                    .map {
                        lv3ClassRandomizerClass.createInstance()
                    }.getOrElse { err ->
                        throw err.toException()
                    }
            }else{
                null
            }
            lv3Rdm
        }

        return randomByLv(
            classData = classData,
            lv1Randomizer = lv1Randomizer,
            lv2RandomizerLz = lv2RandomizerClassLz,
            lv3RandomizerLz = lv3RandomizerLz,
        )
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
                   val lv2paramClassOrParamRandomizer = param
                       .findAnnotations(Randomizable::class).firstOrNull()
                       ?.getClassRandomizerOrParamRandomizer()
                       ?.getOrElse { err ->
                           throw err.toException()
                       }

                   // At this point, lv1 randomizer cannot be used, so move on to check lv2 and below randomizers
                   val lv2ClassRandomizer0 = lv2paramClassOrParamRandomizer?.first?.let { lv2Rd ->
                       randomizerChecker.checkValidRandomizerClassOrThrow(lv2Rd, classifier)
                       lv2Rd.createInstance()
                   }

                   val lv2ParamRandomizer0 = lv2paramClassOrParamRandomizer?.second?.let { lv2Rd ->
                       randomizerChecker.checkValidRandomizerClassOrThrow(lv2Rd, classifier)
                       lv2Rd.createInstance()
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
                            ?.getClassRandomizerOrParamRandomizer()
                            ?.getOrElse { err ->
                                throw err.toException()
                            }

                        val lv2ClassRandomizer0 = lv2paramClassOrParamRandomizer?.first?.let { lv2Rd ->
                            randomizerChecker.checkValidRandomizerClassRs(
                                randomizerClass = lv2Rd,
                                targetClass = parameterData.kClass
                            )
                            lv2Rd.createInstance()
                        }

                        val lv2ParamRandomizer0 = lv2paramClassOrParamRandomizer?.second?.let { lv2Rd ->
                            randomizerChecker.checkValidParamRandomizer(
                                parentClassData = enclosingClassData,
                                targetParam = param,
                                targetTypeParam = classifier,
                                randomizerClass = lv2Rd
                            )
                            lv2Rd.createInstance()
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

    private fun randomChildren(paramKType: KType, parentClassData: RDClassData): Any? {
        // this level does not contain param type, and it must not, because it is also used to generate non-parameter
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
     * lv4 is default randomizer, only used to generate random instances of primitive data types.
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun lv4Random(classData: RDClassData): Any? {
        val classRef: KClass<*> = classData.kClass
        val rt = when (classRef) {
            Byte::class -> random.nextBytes(1)[0]
            Short::class -> random.nextInt().toShort()
            Boolean::class -> random.nextBoolean()
            Int::class -> random.nextInt()
            Long::class -> random.nextLong()
            Double::class -> random.nextDouble()
            Float::class -> random.nextFloat()
            Char::class -> randomChar()
            String::class -> randomString(random)
            List::class, Collection::class -> makeRandomList(classData)
            Map::class -> makeRandomMap(classData)
            Set::class -> makeRandomList(classData).toSet()
            else -> null
        }
        return rt
    }
    private val rdCollectionSize: IntRange = 1..5
    private val rdStringSize: IntRange = 1..10

    private fun makeRandomList(classData: RDClassData): List<Any?> {
        val type: KType = classData.kType
        val numOfElements = random.nextInt(rdCollectionSize.start, rdCollectionSize.endInclusive + 1)
        val elemType = type.arguments[0].type!!
        return (1..numOfElements).map { randomChildren(elemType, classData) }
    }

    private fun makeRandomMap(classData: RDClassData): Map<Any?, Any?> {
        val type: KType = classData.kType
        val numOfElements = random.nextInt(rdCollectionSize.start, rdCollectionSize.endInclusive + 1)
        val keyType = type.arguments[0].type!!
        val valType = type.arguments[1].type!!
        val keys = (1..numOfElements).map { randomChildren(keyType, classData) }
        val values = (1..numOfElements).map { randomChildren(valType, classData) }
        return keys.zip(values).toMap()
    }

    private val charRange = ('A'..'z')

    private fun randomChar(): Char {
        return charRange.random(random)
    }

    private fun randomString(random: Random): String {
        return (1..random.nextInt(
            rdStringSize.start,
            rdStringSize.endInclusive + 1
        )).map { randomChar() }.joinToString(separator = "") { "$it" }
    }
}
