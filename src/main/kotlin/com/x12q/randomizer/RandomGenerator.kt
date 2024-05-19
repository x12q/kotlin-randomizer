package com.x12q.randomizer

import com.github.michaelbull.result.*
import com.x12q.randomizer.annotations.Randomizer
import com.x12q.randomizer.annotations.Randomizer.Companion.getClassRandomizerOnlyRs
import com.x12q.randomizer.annotations.Randomizer.Companion.getClassRandomizerOrParamRandomizerRs
import com.x12q.randomizer.annotations.number._int.RandomIntFixed
import com.x12q.randomizer.annotations.number._int.RandomIntFixed.Companion.makeClassRandomizer
import com.x12q.randomizer.annotations.number._int.RandomIntOneOf
import com.x12q.randomizer.annotations.number._int.RandomIntOneOf.Companion.makeClassRandomizer
import com.x12q.randomizer.annotations.number._int.RandomIntWithin
import com.x12q.randomizer.annotations.number._int.RandomIntWithin.Companion.makeClassRandomizer
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.err.RandomizerErrors
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.RandomizerCollection
import com.x12q.randomizer.randomizer.config.RandomizerConfig
import com.x12q.randomizer.randomizer_checker.RandomizerChecker
import com.x12q.randomizer.util.ReflectionUtils
import com.x12q.randomizer.util.getEnumValue
import javax.inject.Inject
import kotlin.jvm.Throws
import kotlin.random.Random
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.primaryConstructor

/**
 * This class handle the main randomizing logic.
 * There are 4 type(lv) of randomizers used in this class:
 * - lv1: randomizers provided by users explicitly via the top-level random function.
 * - lv2: randomizers from annotations on parameters
 * - lv3: randomizers from annotations on classes
 * - lv4: default randomizers baked into the logic of this class.
 */
data class RandomGenerator @Inject constructor(
    private val random: Random,
    val lv1RandomizerCollection: RandomizerCollection,
    val randomizerChecker: RandomizerChecker,
    val defaultRandomConfig: RandomizerConfig,
) {

    constructor(randomContext: RandomContext) : this(
        random = randomContext.random,
        lv1RandomizerCollection = randomContext.lv1RandomizerCollection,
        randomizerChecker = randomContext.randomizerChecker,
        defaultRandomConfig = randomContext.defaultRandomConfig,
    )

    /**
     * Merge the content of this with another [RandomGenerator]
     */
    fun mergeWith(another: RandomGenerator): RandomGenerator {
        return this.copy(
            lv1RandomizerCollection = this.lv1RandomizerCollection.mergeWith(another.lv1RandomizerCollection)
        )
    }

    /**
     * Produce a [RandomContext] object from the content of this
     */
    fun makeContext(): RandomContext {
        val context = RandomContext(
            random = random,
            lv1RandomizerCollection = lv1RandomizerCollection,
            randomizerChecker = randomizerChecker,
            defaultRandomConfig = defaultRandomConfig,
        )
        return context
    }

    /**
     * Create a random instance of a class represented by [classData]
     */
    fun random(classData: RDClassData): Any? {
        return random(
            classData = classData,
            lv2RandomizerClassLz = null,
            typeMap = classData.directTypeMap,
        )
    }

    /**
     * Create a random instance of a class represented by [classData].
     * [typeMap] is a map of generic type to class data.
     * The [typeMap] passed to this function must guarantee that it provides all the concrete class data for all the generic type that the class need.
     */
    internal fun random(
        classData: RDClassData,
        lv2RandomizerClassLz: Lazy<ClassRandomizer<*>?>?,
        typeMap: Map<String, RDClassData>,
    ): Any? {
        val targetClass: KClass<*> = classData.kClass
        val objectInstance = targetClass.objectInstance
        if (objectInstance != null) {
            return objectInstance
        } else {
            /**
             * lv1 = randomizer is provided explicitly by the users in the top-level random function
             * No need to check for return type because the lv1RandomizerCollection already covers that.
             */
            val lv1Randomizer = lv1RandomizerCollection.getRandomizer(classData)

            /**
             * lv3 randomizers are randomizer provided by annotation on classes
             */
            val lv3RandomizerLz = lazy { getLv3Randomizer(targetClass) }

            return randomByLv(
                classData = classData,
                lv1Randomizer = lv1Randomizer,
                lv2RandomizerLz = lv2RandomizerClassLz,
                lv3RandomizerLz = lv3RandomizerLz,
                typeMap = typeMap,
            )
        }
    }

    /**
     * Make a random instance of an inner class represented by [classData].
     * [outerObj] is the object containing such inner class.
     */
    fun randomInnerClass(classData: RDClassData, outerObj: Any?): Any? {
        return randomInnerClass(
            innerClassData = classData,
            enclosingObject = outerObj,
            lv2RandomizerClassLz = null,
            typeMap = classData.directTypeMap
        )
    }

    private fun randomInnerClass(
        innerClassData: RDClassData,
        enclosingObject: Any?,
        lv2RandomizerClassLz: Lazy<ClassRandomizer<*>?>?,
        typeMap: Map<String, RDClassData>,
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
            val combineTypeMap = innerClassData.makeCombineTypeMap(typeMap)
            return randomByLv(
                classData = innerClassData,
                outerObj = enclosingObject,
                lv1Randomizer = lv1Randomizer,
                lv2RandomizerLz = lv2RandomizerClassLz,
                lv3RandomizerLz = lv3RandomizerLz,
                typeMap = combineTypeMap,
            )
        }
    }


    /**
     * This function create a random instance of some class represented by [classData].
     * To do that, this function goes through multiple randomizer options, from lv1 to lv4.
     * - lv1 randomizers are those provided directly by users via [lv1RandomizerCollection]
     * - lv2 randomizers are those provided by the annotation [Randomizer] at parameters in constructor
     * - lv3 randomizers are those provided by the annotation [Randomizer] at class
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
        lv3RandomizerLz: Lazy<ClassRandomizer<*>?>? = null,
        typeMap: Map<String, RDClassData>,
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

        val rdEnumAndPrim = randomEnumAndPrimitives(
            classData = classData,
            typeMap = typeMap,
        )

        if (rdEnumAndPrim != null) {
            return rdEnumAndPrim
        }

        if (targetClass.isAbstract) {
            randomFunction(classData, typeMap)
            throw IllegalArgumentException("can't randomized abstract class ${targetClass.simpleName}. The only way to generate random instances of abstract class is either provide a randomizer via @${Randomizer::class.simpleName} or via the random function")
        } else if (targetClass.isSealed) {

            return randomSealClass(classData, typeMap)

        } else {
            /**
             * At this point, if there are any constructors with valid randomizers, they are already chosen, so here, we can ignore the annotation's content.
             */
            val constructor = pickConstructorButIgnoreAnnotationContent(targetClass)

            if (constructor != null) {
                try {
                    if (targetClass.isInner) {
                        return callInnerClassConstructor(constructor, classData, outerObj, typeMap)
                    } else {
                        return callClassConstructor(constructor, classData, typeMap)
                    }
                } catch (e: Throwable) {
                    throw e
                }
            } else {
                throw IllegalArgumentException("A primary constructor is need to make a random instance of ${targetClass.qualifiedName}$.")
            }
        }
    }

    /**
     * Call [constructor] of a class represented by [classData].
     * [typeMap] passed to this function must guarantee that it can provide all concrete class data for all generic type in such class.
     */
    private fun callClassConstructor(
        constructor: KFunction<Any?>,
        classData: RDClassData,
        typeMap: Map<String, RDClassData>
    ): Any? {

        val parameters = constructor.parameters

        val arguments = parameters
            .map { constructorParam ->
                randomConstructorParameter(
                    kParam = constructorParam,
                    enclosingClassData = classData,
                    typeMap = typeMap,
                )
            }.toTypedArray()

        val rt = constructor.call(*arguments)

        return rt
    }

    /**
     * Call [constructor] of an inner class represented by [classData]. The inner class is inside [outerObj].
     * [typeMap] passed to this function must guarantee that it can provide all concrete class data for all generic type in such class.
     *
     * Inner classes' constructors are invoked differently from normal class.
     * The constructor always receive the outer object as the first parameter
     */
    private fun callInnerClassConstructor(
        constructor: KFunction<Any?>,
        classData: RDClassData,
        outerObj: Any?,
        typeMap: Map<String, RDClassData>
    ): Any? {

        val targetClass = classData.kClass

        if (!targetClass.isInner) {
            throw IllegalArgumentException("This function is only used for inner class. [${targetClass}] is not an inner class.")
        }

        if (outerObj == null) {
            throw IllegalArgumentException("Outer object of an inner class ([${targetClass}]) must not be null.")
        }

        val parameters = constructor
            .parameters
            .takeLast(constructor.parameters.size - 1)

        val arguments = parameters
            .map { constructorParam ->
                randomConstructorParameter(
                    kParam = constructorParam,
                    enclosingClassData = classData,
                    typeMap = typeMap,
                )
            }.toTypedArray()

        val rt = constructor.call(outerObj, *arguments)
        return rt
    }

    /**
     * For seal classes, a random subclass is picked for randomization.
     * [typeMap] + type data in the chosen subclass must guarantee that they can provide all concrete class data for all generic types in the chosen class.
     */
    private fun randomSealClass(
        classData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): Any? {
        val targetClass: KClass<*> = classData.kClass
        if (!targetClass.isSealed) {
            throw IllegalArgumentException("This function is for sealed class only. [${targetClass}] is not a sealed class.")
        }
        val sealedSubClassList = targetClass.sealedSubclasses
        val pickedSealClass = sealedSubClassList.random()

        val sealClassRdData = RDClassData(
            kClass = pickedSealClass,
            kType = null,
        )
        val combinedTypeMap = sealClassRdData.makeCombineTypeMap(typeMap)
        return random(
            classData = sealClassRdData,
            lv2RandomizerClassLz = null,
            typeMap = combinedTypeMap,
        )
    }


    @Throws(Throwable::class)
    private fun randomConstructorParameter(
        kParam: KParameter,
        enclosingClassData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): Any? {
        val rs = randomConstructorParameterRs(
            param = kParam,
            enclosingClassData = enclosingClassData,
            typeMap = typeMap,
        )
        val rt = rs.getOrElse { err ->
            throw err.toException()
        }
        return rt
    }

    /**
     * enclosing class is the class that use [param] in its constructor.
     * For example:
     * class ABC(val i:Int)
     * For "val i", ABC is the enclosing class
     *
     * [rdChain] already contain [enclosingClassData]
     */
    private fun randomConstructorParameterRs(
        param: KParameter,
        enclosingClassData: RDClassData,
        /**
         * This is accumulative type map calculated up to the point of enclosing class.
         * Not yet including data from the class of [param].
         * That is because at this point, it is still unclear if a more detail type map is needed or can be computed or not.
         * That depends on the type of [param], which will be found out inside this function.
         */
        typeMap: Map<String, RDClassData>,
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
                    makeLv2ForKClass(
                        param = param,
                        paramData = paramData,
                        enclosingClassData = enclosingClassData,
                        kClass = classifier
                    )
                }

                val combinedTypeMap = paramData.makeCombineTypeMap(typeMap)

                val rt = random(
                    classData = paramData,
                    lv2RandomizerClassLz = lv2Lz,
                    typeMap = combinedTypeMap
                )
                return Ok(rt)
            }

            is KTypeParameter -> {

                /**
                 * This is for generic-type parameters
                 * such as: class Q<T>(val s:T)
                 */

                val parameterData: RDClassData? = typeMap[classifier.name]
                if (parameterData != null) {

                    // lv2 is extracted + type check here, then passed to random(). Within random(), it will be decided lv2 will be used or not.
                    val combineTypeMap = parameterData.makeCombineTypeMap(typeMap)

                    val lv2Lz = lazy {
                        makeLv2ForTypeParam(
                            param = param,
                            parameterData = parameterData,
                            enclosingClassData = enclosingClassData,
                            kTypeParam = classifier
                        )
                    }

                    return Ok(
                        random(
                            classData = parameterData,
                            lv2RandomizerClassLz = lv2Lz,
                            typeMap = combineTypeMap
                        )
                    )

                } else {
                    return Err(RandomizerErrors.TypeDoesNotExist.report(classifier, enclosingClassData))
                }
            }

            else -> return Err(RandomizerErrors.ClassifierNotSupported.report(classifier))
        }
    }


    /**
     * lv4 is the default randomizer, only used to generate random instances of primitive data types.
     */

    private fun randomEnumAndPrimitives(
        classData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): Any? {
        return lv4EnumRandom(classData) ?: lv4RandomPrimitive(classData, typeMap)
    }

    private fun lv4EnumRandom(classData: RDClassData): Any? {
        return getEnumValue(classData.kClass)?.random(random)
    }

    private fun randomFunction(
        classData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ) {
        val clzz = classData.kClass
        if (clzz is Function<*>) {
            throw IllegalArgumentException("does not support function")
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun lv4RandomPrimitive(
        classData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): Any? {

        val clzz: KClass<*> = classData.kClass

        val primitive: Any? = when (clzz) {
            Number::class -> randomNumber()
            Int::class -> random.nextInt()
            Long::class -> random.nextLong()
            Float::class -> random.nextFloat()
            Double::class -> random.nextDouble()
            Char::class -> randomChar()
            CharSequence::class, String::class -> randomString(random)
            Boolean::class -> random.nextBoolean()
            Byte::class -> random.nextBytes(1)[0]
            Short::class -> random.nextInt().toShort()
            else -> null
        }

        if (primitive == null) {
            val rt = if (clzz.isSuperclassOf(List::class)) {
                val list = makeRandomList(
                    classData = classData,
                    typeMap = typeMap,
                )
                if (clzz.isSuperclassOf(MutableList::class)) {
                    list.toMutableList()
                } else {
                    list
                }
            } else if (clzz.isSuperclassOf(Map::class)) {
                val mp = makeRandomMap(
                    mapClassData = classData,
                    typeMap = typeMap,
                )
                if (clzz.isSuperclassOf(MutableMap::class)) {
                    mp.toMutableMap()
                } else {
                    mp
                }
            } else if (clzz.isSuperclassOf(Set::class)) {

                val st = makeRandomList(
                    classData = classData,
                    typeMap = typeMap,
                ).toSet()
                if (clzz.isSuperclassOf(MutableSet::class)) {
                    st.toMutableSet()
                } else {
                    st
                }
            } else {
                null
            }
            return rt
        } else {
            return primitive
        }
    }

    private fun randomNumber(): Number {
        return listOf(random.nextInt(), random.nextDouble(), random.nextFloat(), random.nextLong()).random()
    }

    private val collectionSize: IntRange = defaultRandomConfig.collectionSize
    private val strSize: IntRange = defaultRandomConfig.stringSize

    private fun randomArray(
        classData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): Array<Any?> {

        val type: KType? = classData.kType
        val combineTypeMap = classData.makeCombineTypeMap(typeMap)

        if (type != null) {
            val numOfElements = random.nextInt(collectionSize.first, collectionSize.last + 1)
            val elementType = classData.kClass.typeParameters[0].name.let {
                typeMap[it]?.kType!!
            }
            val rt = (1..numOfElements).map {
                randomElement(
                    paramKType = elementType,
                    upperTypeMap = combineTypeMap
                )
            }.toTypedArray()
            return rt
        } else {
            throw IllegalArgumentException("Unable to get Ktype, therefore can't to generate random List")
        }
    }

    private fun makeRandomList(
        classData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): List<Any?> {

        val type: KType? = classData.kType
        val combineTypeMap = classData.makeCombineTypeMap(typeMap)

        if (type != null) {
            val numOfElements = random.nextInt(collectionSize.first, collectionSize.last + 1)
            val elementType = classData.kClass.typeParameters[0].name.let {
                typeMap[it]?.kType!!
            }
            return (1..numOfElements).map {
                randomElement(
                    paramKType = elementType,
                    upperTypeMap = combineTypeMap
                )
            }
        } else {
            throw IllegalArgumentException("Unable to get Ktype, therefore can't to generate random List")
        }
    }

    private fun makeRandomMap(
        mapClassData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): Map<Any?, Any?> {

        val type: KType? = mapClassData.kType
        val combinedTypeMap = mapClassData.makeCombineTypeMap(typeMap)

        if (type != null) {
            val elementCount = random.nextInt(collectionSize.first, collectionSize.last + 1)
            val declaredTypeParams: List<KTypeParameter> = mapClassData.kClass.typeParameters

            val keys = run {
                val keyType = declaredTypeParams[0].name.let {
                    typeMap[it]?.kType!!
                }
                (1..elementCount).map {
                    randomElement(
                        paramKType = keyType,
                        upperTypeMap = combinedTypeMap,
                    )
                }
            }

            val values = run {
                val valueType = declaredTypeParams[1].name.let {
                    typeMap[it]?.kType!!
                }
                (1..elementCount).map {
                    randomElement(
                        paramKType = valueType,
                        upperTypeMap = combinedTypeMap,
                    )
                }
            }
            return keys.zip(values).toMap()
        } else {
            throw IllegalArgumentException("Unable to get Ktype, therefore can't to generate random Map")
        }
    }

    /**
     * Example:
     * class Q<T>(val l:List<T>)
     * - paramKType = T
     * - parent class = List
     * - enclosing class = Q
     */
    private fun randomElement(
        paramKType: KType,
        upperTypeMap: Map<String, RDClassData>,
    ): Any? {

        when (val classifier = paramKType.classifier) {
            is KClass<*> -> {
                /**
                 * This is for normal parameter
                 */
                val paramClassData = RDClassData(classifier, paramKType)
                val typeMap = paramClassData.makeCombineTypeMap(upperTypeMap)
                val element = random(
                    classData = paramClassData,
                    lv2RandomizerClassLz = null,
                    typeMap = typeMap,
                )
                return element
            }
            /**
             * This is for generic-type parameters
             * such as: class Q<T>(val s:T)
             */
            is KTypeParameter -> {
                val parameterData: RDClassData? = upperTypeMap[classifier.name]

                if (parameterData != null) {
                    val typeMap = parameterData.makeCombineTypeMap(upperTypeMap)
                    val element = random(
                        classData = parameterData,
                        lv2RandomizerClassLz = null,
                        typeMap = typeMap,
                    )
                    return element
                } else {
                    throw IllegalArgumentException("type does not exist for ${classifier} in ${upperTypeMap}")
                }
            }

            else -> throw Error("Type of the classifier $classifier is not supported")
        }
    }

    private val charRange = defaultRandomConfig.charRange

    private fun randomString(random: Random): String {
        val range = (1..random.nextInt(strSize.first, strSize.last + 1))
        return range
            .map { randomChar() }
            .joinToString(separator = "") { "$it" }
    }

    private fun randomChar(): Char {
        return charRange.random(random)
    }

    /**
     * Create lv2 randomizer for KClass, used in [randomConstructorParameterRs]
     */
    private fun makeLv2ForKClass(
        param: KParameter,
        paramData: RDClassData,
        enclosingClassData: RDClassData,
        kClass: KClass<*>,
    ): ClassRandomizer<Any?>? {
        val lv2paramClassOrParamRandomizer: Pair<KClass<out ClassRandomizer<*>>?, KClass<out ParameterRandomizer<*>>?>? =
            param
                .findAnnotations(Randomizer::class).firstOrNull()
                ?.getClassRandomizerOrParamRandomizerRs()
                ?.getOrElse { err ->
                    throw err.toException()
                }

        val lv2ClassRandomizer = lv2paramClassOrParamRandomizer?.first?.let { lv2Rd ->
            randomizerChecker.checkValidRandomizerClassOrThrow(lv2Rd, kClass)
            ReflectionUtils.createClassRandomizer(lv2Rd)
        }

        val lv2ParamRandomizer0 = lv2paramClassOrParamRandomizer?.second?.let { lv2Rd ->
            randomizerChecker.checkValidRandomizerClassOrThrow(lv2Rd, kClass)
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

        /**
         * Prioritize param randomizer over class randomizer
         */
        val intRandomizers = getLv2IntRandomizer(param, paramData)
        val randomizers = listOfNotNull(lv2ParamRandomizer, lv2ClassRandomizer) + intRandomizers
        val lv2Randomizer = randomizers.randomOrNull()

        return lv2Randomizer

    }

    /**
     * Create lv2 randomizer for KTypeParam, used in [randomConstructorParameterRs]
     */
    private fun makeLv2ForTypeParam(
        param: KParameter,
        parameterData: RDClassData,
        enclosingClassData: RDClassData,
        kTypeParam: KTypeParameter,
    ): ClassRandomizer<Any?>? {
        val lv2paramClassOrParamRandomizer = param
            .findAnnotations(Randomizer::class).firstOrNull()
            ?.getClassRandomizerOrParamRandomizerRs()
            ?.getOrElse { err ->
                throw err.toException()
            }

        val lv2ClassRandomizer = lv2paramClassOrParamRandomizer?.first?.let { lv2Rd ->
            randomizerChecker.checkValidRandomizerClassRs(
                randomizerClass = lv2Rd,
                targetClass = parameterData.kClass
            ) // TODO throw this if err
            ReflectionUtils.createClassRandomizer(lv2Rd)
        }

        val lv2ParamRandomizer0 = lv2paramClassOrParamRandomizer?.second?.let { lv2Rd ->
            randomizerChecker.checkValidParamRandomizer(
                enclosingClassData = enclosingClassData,
                targetParam = param,
                targetTypeParam = kTypeParam,
                randomizerClass = lv2Rd
            ) // TODO throw this if err
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

        val intRandomizers = getLv2IntRandomizer(param, parameterData)
        val randomizers = listOfNotNull(lv2ParamRandomizer, lv2ClassRandomizer) + intRandomizers
        val lv2Randomizer = randomizers.randomOrNull()
        return lv2Randomizer
    }

    fun getLv2IntRandomizer(
        param: KParameter,
        parameterData: RDClassData,
    ): List<ClassRandomizer<Any?>> {
        val rt = listOfNotNull(
            getRandomizerFrom_RandomIntFixed(param, parameterData) as? ClassRandomizer<Any?>,
            getRandomizerFrom_RandomIntOneOf(param, parameterData) as? ClassRandomizer<Any?>,
            getRandomizerFrom_RandomIntWithin(param, parameterData) as? ClassRandomizer<Any?>,
        )
        return rt
    }

    private fun getRandomizerFrom_RandomIntWithin(
        param: KParameter,
        parameterData: RDClassData,
    ): ClassRandomizer<Int>? {
        return getRandomizerFromAnnotation<RandomIntWithin, Int>(
            param = param,
            parameterData = parameterData,
            annotationClass = RandomIntWithin::class,
            extractRandomizerFromAnnotation = { annotation ->
                annotation.makeClassRandomizer()
            })
    }

    private fun getRandomizerFrom_RandomIntOneOf(
        param: KParameter,
        parameterData: RDClassData,
    ): ClassRandomizer<Int>? {
        return getRandomizerFromAnnotation<RandomIntOneOf, Int>(
            param = param,
            parameterData = parameterData,
            annotationClass = RandomIntOneOf::class,
            extractRandomizerFromAnnotation = { annotation ->
                annotation.makeClassRandomizer()
            })
    }

    private fun getRandomizerFrom_RandomIntFixed(
        param: KParameter,
        parameterData: RDClassData,
    ): ClassRandomizer<Int>? {
        return getRandomizerFromAnnotation<RandomIntFixed, Int>(
            param = param,
            parameterData = parameterData,
            annotationClass = RandomIntFixed::class,
            extractRandomizerFromAnnotation = { annotation ->
                annotation.makeClassRandomizer()
            })
    }


    /**
     * Extract randomizer from annotation of type [T] on [param] by calling [extractRandomizerFromAnnotation].
     */
    private inline fun <T : Annotation, reified V> getRandomizerFromAnnotation(
        param: KParameter,
        parameterData: RDClassData,
        annotationClass: KClass<T>,
        extractRandomizerFromAnnotation: (T) -> ClassRandomizer<V>
    ): ClassRandomizer<V>? {
        val annotation = param.findAnnotations(annotationClass).firstOrNull()
        if (annotation != null) {
            val rt = if (parameterData.kClass == V::class) {
                extractRandomizerFromAnnotation(annotation)
            } else {
                /**
                 * Can't use annotation [T] on non-[V] parameter
                 */
                throw IllegalArgumentException("Can't use annotation [${annotationClass}] on [${param}]")
            }
            return rt
        } else {
            return null
        }
    }


    /**
     * Throw an exception if a [ClassRandomizer] is not applicable to [rdClassData]
     */
    private fun ClassRandomizer<*>.throwIfNotApplicableTo(rdClassData: RDClassData) {
        if (!this.isApplicableTo(rdClassData)) {
            throw RandomizerErrors.CantApplyClassRandomizerToClass.report(this, rdClassData.kClass).toException()
        }
    }

    /**
     * Pick a random constructor from [targetClass].
     *
     * The order of priority is:
     * - richly annotated constructor > poorly annotated constructor > primary constructor > other constructors
     *
     * A richly annotated constructor is one that annotated with [Randomizer] and with a valid randomizer
     * A poor annotated constructor is one that annotated with [Randomizer] and without any randomizer
     *
     */
    internal fun pickConstructor(targetClass: KClass<*>): PickConstructorResult? {

        val constructors: Collection<KFunction<Any>> = targetClass.constructors

        /**
         * A rich annotated constructor is one that annotated with [Randomizer] and with a valid randomizer
         */
        val richConstructors: MutableList<PickConstructorResult> = mutableListOf()

        /**
         * A poor annotated constructor is one that annotated with [Randomizer] and without any randomizer
         */
        val poorConstructors: MutableList<KFunction<Any>> = mutableListOf()

        var stillConsiderPoorAnnotatedConstructor = true

        for (con in constructors) {

            val annotationList = con.findAnnotations<Randomizer>()

            if (annotationList.isNotEmpty()) {
                for (annotation in annotationList) {

                    val rdmClass = annotation
                        .getClassRandomizerOnlyRs(targetClass)
                        .getOrElse { typeErr -> throw typeErr.toException() }

                    if (rdmClass != null) {
                        randomizerChecker.checkValidRandomizerClassOrThrow(rdmClass, targetClass)
                        stillConsiderPoorAnnotatedConstructor = false
                        richConstructors.add(PickConstructorResult(con, rdmClass))
                    }
                }
                if (stillConsiderPoorAnnotatedConstructor) {
                    poorConstructors.add(con)
                }
            }
        }

        if (richConstructors.isNotEmpty()) {
            return richConstructors.random()
        }

        if (poorConstructors.isNotEmpty()) {
            /**
             * Pick a random from the poorly annotated constructors
             */
            return PickConstructorResult(
                poorConstructors.random(), null
            )
        }

        /**
         * Use primary constructor if there are not any annotated constructor
         */

        val nonAnnotatedConstructor = targetClass.primaryConstructor ?: targetClass.constructors.randomOrNull()
        return nonAnnotatedConstructor?.let {
            PickConstructorResult(it, null)
        }
    }

    /**
     * Pick a random constructor among constructors annotated with [Randomizer] in [targetClass].
     * If none is found, return the primary constructor.
     */
    private fun pickConstructorButIgnoreAnnotationContent(targetClass: KClass<*>): KFunction<Any>? {
        val annotatedConstructors = targetClass.constructors.filter {
            it.findAnnotations<Randomizer>().firstOrNull() != null
        }
        if (annotatedConstructors.isNotEmpty()) {
            /**
             * If there are multiple annotated constructor, pick a random one
             */
            val annotatedConstructor = annotatedConstructors.random(random)

            return annotatedConstructor
        } else {
            val primaryConstructor = targetClass.primaryConstructor
            if (primaryConstructor != null) {
                return primaryConstructor
            } else {
                // java class does not have primary constructor
                return targetClass.constructors.randomOrNull()
            }

        }
    }


    /**
     * Get lv3 randomizer from a class.
     * Throw an error if a randomizer exist, but of the wrong type.
     */
    @Throws(Throwable::class)
    internal fun getLv3Randomizer(targetClass: KClass<*>): ClassRandomizer<*>? {
        /**
         * First, extract the randomizer class in the [Randomizer] on the class
         */
        val classRdmRs = targetClass.findAnnotations(Randomizer::class)
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
            val lv3 = constructorRs2?.lv3RandomizerClass
            if (lv3 != null) {
                return ReflectionUtils.createClassRandomizer(lv3)
            }
            return null
        }
    }

}
