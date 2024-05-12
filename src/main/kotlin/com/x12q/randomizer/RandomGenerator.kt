package com.x12q.randomizer

import com.github.michaelbull.result.*
import com.x12q.randomizer.Randomizable.Companion.getClassRandomizerOnlyRs
import com.x12q.randomizer.Randomizable.Companion.getClassRandomizerOrParamRandomizerRs
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
import kotlin.reflect.full.primaryConstructor


data class RandomGenerator @Inject constructor(
    private val random: Random,
    val lv1RandomizerCollection: RandomizerCollection,
    val randomizerChecker: RandomizerChecker,
    val defaultRandomConfig: RandomizerConfig,
) {

    fun random(classData: RDClassData): Any? {
        return random(
            classData = classData,
            lv2RandomizerClassLz = null,
            typeMap = classData.directTypeMap,
        )
    }

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
            // lv1 = randomizer is provided explicitly by the users in the top-level random function
            // No need to check for return type because the lv1RandomizerCollection already covers that.
            val lv1Randomizer = lv1RandomizerCollection.getRandomizer(classData)

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

            throw IllegalArgumentException("can't randomized abstract class ${targetClass.simpleName}. The only way to generate random instances of abstract class is either provide a randomizer via @${Randomizable::class.simpleName} or via the random function")

        } else if (targetClass.isSealed) {

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

        } else {
            /**
             * At this point, if there are any constructors with valid randomizers, they are already chosen, so here, we can ignore the annotation's content.
             */
            val constructor = pickConstructorButIgnoreAnnotationContent(targetClass)

            if (constructor != null) {
                try {
                    if (targetClass.isInner) {

                        val arguments = constructor.parameters
                            .takeLast(constructor.parameters.size - 1)
                            .map { constructorParam ->
                                randomConstructorParameter(
                                    kParam = constructorParam,
                                    parentClassData = classData,
                                    typeMap = typeMap,
                                )
                            }.toTypedArray()

                        return constructor.call(outerObj, *arguments)

                    } else {
                        val arguments = constructor.parameters
                            .map { constructorParam ->
                                randomConstructorParameter(
                                    kParam = constructorParam,
                                    parentClassData = classData,
                                    typeMap = typeMap,
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

    @Throws(Throwable::class)
    fun randomConstructorParameter(
        kParam: KParameter,
        parentClassData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): Any? {
        val rs = randomConstructorParameterRs(
            param = kParam,
            enclosingClassData = parentClassData,
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
    fun randomConstructorParameterRs(
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
                            ktypeParam = classifier
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

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun lv4RandomPrimitive(
        classData: RDClassData,
        typeMap: Map<String, RDClassData>,
    ): Any? {
        val clzz: KClass<*> = classData.kClass

        val primitive = when (clzz) {
            Char::class -> randomChar()
            Int::class -> random.nextInt()
            Long::class -> random.nextLong()
            Float::class -> random.nextFloat()
            Double::class -> random.nextDouble()
            String::class -> randomString(random)
            Boolean::class -> random.nextBoolean()
            Byte::class -> random.nextBytes(1)[0]
            Short::class -> random.nextInt().toShort()
            List::class, Collection::class, Iterable::class -> {
                makeRandomList(
                    classData = classData,
                    typeMap = typeMap,
                )
            }

            Map::class -> makeRandomMap(
                mapClassData = classData,
                typeMap = typeMap,
            )

            Set::class -> makeRandomList(
                classData = classData,
                typeMap = typeMap,
            ).toSet()

            else -> null
        }

        val rt = primitive

        return rt

    }

    private val collectionSize: IntRange = defaultRandomConfig.collectionSize
    private val strSize: IntRange = defaultRandomConfig.stringSize

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
                .findAnnotations(Randomizable::class).firstOrNull()
                ?.getClassRandomizerOrParamRandomizerRs()
                ?.getOrElse { err ->
                    throw err.toException()
                }

        // At this point, lv1 randomizer cannot be used, so move on to check lv2 and below randomizers
        val lv2ClassRandomizer0 = lv2paramClassOrParamRandomizer?.first?.let { lv2Rd ->
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

        val lv2ClassRandomizer = lv2ParamRandomizer ?: lv2ClassRandomizer0
        return lv2ClassRandomizer

    }

    /**
     * Create lv2 randomizer for KTypeParam, used in [randomConstructorParameterRs]
     */
    private fun makeLv2ForTypeParam(
        param: KParameter,
        parameterData: RDClassData,
        enclosingClassData: RDClassData,
        ktypeParam: KTypeParameter,
    ): ClassRandomizer<Any?>? {
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
                targetTypeParam = ktypeParam,
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
        return lv2ClassRandomizer
    }


    internal fun pickConstructor(targetClass: KClass<*>): PickConstructorResult? {

        val constructors: Collection<KFunction<Any>> = targetClass.constructors
        if (targetClass == ArrayList::class) {
            val con = constructors.first {
                val c1 = it.parameters.size == 1
                val c2 = it.parameters[0].type.classifier == Collection::class
                c1 && c2
            }
            return PickConstructorResult(con, null)
        }

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

        if (targetClass == ArrayList::class) {
            val con = constructors.toList()[0]
            return PickConstructorResult(con, null)
        }
        val nonAnnotatedConstructor = targetClass.primaryConstructor ?: targetClass.constructors.randomOrNull()
        return nonAnnotatedConstructor?.let {
            PickConstructorResult(it, null)
        }
    }

    /**
     * Pick a random constructor among constructors annotated with [Randomizable] in [targetClass].
     * If none is found, return the primary constructor.
     */
    private fun pickConstructorButIgnoreAnnotationContent(targetClass: KClass<*>): KFunction<Any>? {
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
            val lv3 = constructorRs2?.lv3RandomizerClass
            if (lv3 != null) {
                return ReflectionUtils.createClassRandomizer(lv3)
            }
            return null
        }
    }

}
