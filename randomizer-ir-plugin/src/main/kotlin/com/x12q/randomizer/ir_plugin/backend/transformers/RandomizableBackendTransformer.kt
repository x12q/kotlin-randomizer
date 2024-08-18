package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.lib.RandomConfigImp
import com.x12q.randomizer.lib.RandomConfig
import com.x12q.randomizer.lib.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.*
import com.x12q.randomizer.ir_plugin.backend.utils.*
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.stopAtFirstNotNull
import com.x12q.randomizer.lib.RandomContext
import com.x12q.randomizer.lib.RandomContextBuilder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

/**
 * Order of priority: random context > generic factory function
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
    private val randomAccessor: RandomAccessor,
    private val randomConfigAccessor: RandomConfigAccessor,
    private val defaultRandomConfigAccessor: DefaultRandomConfigAccessor,
    private val function0Accessor: Function0Accessor,
    private val function1Accessor: Function1Accessor,
    private val randomizerContextBuilderAccessor: RandomizerContextBuilderAccessor,
    private val randomContextBuilderImpAccessor: RandomContextBuilderImpAccessor,
    private val randomizerCollectionAccessor: RandomizerCollectionAccessor,
    private val randomContextAccessor: RandomContextAccessor,
) : RDBackendTransformer() {

    override fun visitClassNew(declaration: IrClass): IrStatement {

        val annotation = declaration.getAnnotation(BaseObjects.randomizableFqName)
        if (annotation != null) {
            val companionObj = declaration.companionObject()
            if (companionObj != null) {
                completeRandomFunction1(companionObj, declaration)
                completeRandomFunction2(companionObj, declaration)
            }
        }
        return super.visitClassNew(declaration)
    }


    override fun visitCall(expression: IrCall): IrExpression {
        println(expression.dumpKotlinLike())
        val returnType = (expression.type.classifierOrNull as? IrClassSymbol)
        val functionName = expression.symbol.owner.name
        val needToGenerateSyntheticArgument = false
        if (needToGenerateSyntheticArgument) {
            // add new synthetic argument here
//            expression.putValueArgument()
        }

        return super.visitCall(expression)

    }

    /**
     * complete random() function in [companionObj].
     * This function use the random config in annotation.
     *
     * ```
     *      fun random(
     *          randomT:RandomConfig.()->T?,
     *          randomT2:RandomConfig.()->T2,
     *          randomizers:RandomContextBuilder.()->Unit = {},
     *      )
     * ```
     */
    private fun completeRandomFunction1(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            val con1 = function.name == BaseObjects.randomFunctionName
            val con2 = function.valueParameters.size == target.typeParameters.size + 1
            val con3 = function.valueParameters.lastOrNull()?.let { firstParam ->
                firstParam.name == BaseObjects.randomContextBuilderConfigFunctionParamName
            } ?: false
            con1 && con2 && con3
        }
        if (randomFunction != null) {
            val annotation = requireNotNull(
                target.getAnnotation(BaseObjects.randomizableFqName)
            ) {
                "At this point, it must be guaranteed that the Randomizable annotation must not null. Cannot run this function on a class that does not have that annotation. This a bug by the developer."
            }
            val builder = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = randomFunction.symbol,
            )
            val typeParamOfRandomFunction: List<IrTypeParameter> = randomFunction.typeParameters
            val getRandomConfigExpr = makeGetRandomConfigExpressionFromAnnotation(annotation, builder)
            val genericRandomFunctionParamList: List<IrValueParameter> = randomFunction.valueParameters.dropLast(1)

            val randomContextBuilderConfigFunctionParam =
                requireNotNull(randomFunction.valueParameters.lastOrNull()) {
                    "randomizers function is missing. This is impossible, and is a bug by developer"
                }

            randomFunction.body = constructRandomFunctionBody(
                builder = builder,
                randomContextBuilderConfigFunctionParam = randomContextBuilderConfigFunctionParam,
                randomFunction = randomFunction,
                getRandomConfigExpr = getRandomConfigExpr,
                target = target,
                typeParamOfRandomFunction = typeParamOfRandomFunction,
                genericRandomFunctionParamList = genericRandomFunctionParamList
            )
            println(randomFunction.dumpKotlinLike())
        }
    }

    private fun constructRandomFunctionBody(
        builder: DeclarationIrBuilder,
        randomContextBuilderConfigFunctionParam: IrValueParameter,
        randomFunction: IrFunction,
        getRandomConfigExpr: IrExpression,
        target: IrClass,
        /**
         * This is the list of all generic type param in the declaration of [randomFunction]
         */
        typeParamOfRandomFunction: List<IrTypeParameter>,
        /**
         * This is the list of generic factory function passed to [randomFunction]
         */
        genericRandomFunctionParamList: List<IrValueParameter>,
    ): IrBlockBody {

        val randomConfigVar = makeRandomConfigVar(randomFunction, getRandomConfigExpr)
        val getRandomConfigFromVar = builder.irGet(randomConfigVar)

        val randomContextVar = makeRandomContextVar(
            builder = builder,
            randomContextBuilderConfigFunctionParam = randomContextBuilderConfigFunctionParam,
            randomFunction = randomFunction,
            getRandomConfig = getRandomConfigFromVar
        )

        val getRandomContext = builder.irGet(randomContextVar)

        val constructorCall = generateRandomClassInstance(
            receivedTypeArguments = emptyList(),
            param = null,
            irClass = target,
            getRandomContextExpr = getRandomContext,
            getRandomConfigExpr = getRandomConfigFromVar,
            builder = builder,
            typeParamOfRandomFunction = typeParamOfRandomFunction,
            genericRandomFunctionParamList = genericRandomFunctionParamList,
        )
        if (constructorCall != null) {
            return builder.irBlockBody {
                +randomConfigVar
                +randomContextVar
                +builder.irReturn(constructorCall)
            }
        } else {
            throw IllegalArgumentException("unable generate constructor call for $target")
        }
    }

    private fun makeRandomConfigVar(
        randomFunction: IrFunction,
        getRandomConfig: IrExpression,
    ): IrVariable {
        return buildVariable(
            parent = randomFunction,
            startOffset = randomFunction.startOffset,
            endOffset = randomFunction.endOffset,
            origin = IrDeclarationOrigin.DEFINED,
            name = Name.identifier("varRandomConfig"),
            type = randomConfigAccessor.irType
        ).withInit(getRandomConfig)
    }

    /**
     * Create a var that holds the newly created [RandomContext]
     */
    private fun makeRandomContextVar(
        builder: DeclarationIrBuilder,
        randomContextBuilderConfigFunctionParam: IrValueParameter,
        randomFunction: IrFunction,
        getRandomConfig: IrExpression,
    ): IrVariable {

        val block = builder.irBlock(
            resultType = randomizerCollectionAccessor.irType,
            origin = BaseObjects.Ir.statementOrigin
        ) {

            val randomContextBuilderVar = irTemporary(
                value = randomContextBuilderImpAccessor.constructorFunction(this),
                nameHint = "randomContextBuilder"
            )

            /**
             * Give base random config to builder
             */
            +irGet(randomContextBuilderVar).dotCall(
                randomizerContextBuilderAccessor.setRandomConfigFunction(builder)
            ).withValueArgs(getRandomConfig)

            /**
             * Run config function over a randomizer collection builder
             */
            +makeExprToConfigRandomContextBuilder(
                getRandomContextBuilderExpr = irGet(randomContextBuilderVar),
                randomizerBuilderConfigFunctionAsParam = randomContextBuilderConfigFunctionParam,
                builder = this
            )

            /**
             * Run the randomizer collection builder to get an updated random config.
             * Then store it in a var.
             */
            val updateRandomConfigVar = irTemporary(
                buildRandomContextExpr(
                    builder = this,
                    getRandomContextBuilderExpr = irGet(randomContextBuilderVar),
                ),
                nameHint = "varRandomContext"
            )

            +irGet(updateRandomConfigVar)
        }

        val varRt = buildVariable(
            parent = randomFunction,
            startOffset = randomFunction.startOffset,
            endOffset = randomFunction.endOffset,
            origin = IrDeclarationOrigin.DEFINED,
            name = Name.identifier("varRandomContext"),
            type = randomContextAccessor.irType
        ).withInit(block)

        return varRt
    }

    private fun buildRandomContextExpr(
        builder: IrBuilderWithScope,
        getRandomContextBuilderExpr: IrExpression,
    ): IrExpression {
        return getRandomContextBuilderExpr.dotCall(randomizerContextBuilderAccessor.buildRandomConfigFunction(builder))
    }

    /**
     * Create an IrExpr to run a config function from [randomizerBuilderConfigFunctionAsParam]  on [RandomContextBuilder] from [getRandomContextBuilderExpr]
     */
    private fun makeExprToConfigRandomContextBuilder(
        getRandomContextBuilderExpr: IrExpression,
        randomizerBuilderConfigFunctionAsParam: IrValueParameter,
        builder: IrBuilderWithScope,
    ): IrExpression {

        val rt = builder.irGet(randomizerBuilderConfigFunctionAsParam)
            .dotCall(function1Accessor.invokeFunction(builder))
            .withValueArgs(getRandomContextBuilderExpr)

        return rt
    }

    /**
     * Complete random(randomConfig) function
     */
    private fun completeRandomFunction2(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            val con1 =
                function.name == BaseObjects.randomFunctionName && function.valueParameters.size == target.typeParameters.size + 2
            val con2 = function.valueParameters.firstOrNull()?.let { firstParam ->
                firstParam.name == BaseObjects.randomConfigParamName
            } ?: false

            con1 && con2
        }

        if (randomFunction != null) {
            val builder = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = randomFunction.symbol,
            )

            val randomConfigParam =
                requireNotNull(randomFunction.valueParameters.firstOrNull { it.name == BaseObjects.randomConfigParamName }) {
                    "random(randomConfig,...) must have first parameter being randomConfig. This is a bug from developer side. "
                }
            val getBaseRandomConfigExpr = builder.irGet(randomConfigParam)
            val typeParamOfRandomFunction: List<IrTypeParameter> = randomFunction.typeParameters
            val genericRandomFunctionParamList: List<IrValueParameter> =
                randomFunction.valueParameters
                    //drop the first argument: it is a RandomConfig, not a generic factory lambda
                    .drop(1)
                    //drop the last argument: it is randomizer builder config lambda, not a generic factory lambda
                    .dropLast(1)

            val randomContextBuilderConfigFunctionParam = requireNotNull(randomFunction.valueParameters.lastOrNull()) {
                "randomizers function is missing. This is impossible, and is a bug by developer"
            }
            randomFunction.body = constructRandomFunctionBody(
                builder = builder,
                randomContextBuilderConfigFunctionParam = randomContextBuilderConfigFunctionParam,
                randomFunction = randomFunction,
                getRandomConfigExpr = getBaseRandomConfigExpr,
                target = target,
                typeParamOfRandomFunction = typeParamOfRandomFunction,
                genericRandomFunctionParamList = genericRandomFunctionParamList
            )
        }
    }

    /**
     * Create an IR expression that returns a [RandomConfig] instance from [Randomizable] annotation
     */
    private fun makeGetRandomConfigExpressionFromAnnotation(
        annotation: IrConstructorCall,
        builder: DeclarationIrBuilder
    ): IrExpression {

        val randomConfigArgumentParamData = annotation
            .getAllArgumentsWithIr()
            .firstOrNull { (irParam, irExpr) ->
                irParam.name == BaseObjects.randomConfigParamName
            }

        val providedArgument = randomConfigArgumentParamData?.second

        if (providedArgument != null) {

            val providedArgumentClassSymbol =
                requireNotNull((providedArgument as? IrClassReference)?.classType?.classOrNull) {
                    "$providedArgument must be a KClass"
                }

            val providedClassIsDefaultRandomConfigClass =
                providedArgumentClassSymbol.owner.classId == BaseObjects.DefaultRandomConfig_ClassId

            if (providedClassIsDefaultRandomConfigClass) {
                return getDefaultRandomConfigInstance(builder)
            } else {
                val providedArgumentIrClass = providedArgumentClassSymbol.owner

                if (providedArgumentIrClass.isObject) {
                    return builder.irGetObject(providedArgumentClassSymbol)
                } else if (providedArgumentIrClass.isClass) {

                    when (providedArgumentIrClass.modality) {
                        ABSTRACT,
                        SEALED -> {
                            throw IllegalArgumentException("${providedArgumentIrClass.name} must not be abstract")
                        }

                        OPEN,
                        FINAL -> {

                            /**
                             * There's a custom [RandomConfig] class, proceed to create an instance of it.
                             * Constructor must be zero-arg in case that is a class, otherwise throw exception
                             */

                            val primaryConstructor = providedArgumentIrClass.primaryConstructor.takeIf {
                                it != null && it.valueParameters.isEmpty()
                            }

                            val constructor = primaryConstructor ?: providedArgumentIrClass.constructors.firstOrNull {
                                it.valueParameters.isEmpty()
                            }

                            if (constructor != null) {
                                return builder.irCall(constructor)
                            } else {
                                throw IllegalArgumentException("${providedArgumentIrClass.name}: must have a zero-arg constructor")
                            }
                        }
                    }

                } else {
                    throw IllegalArgumentException("${providedArgumentIrClass.name} must either be a class or an object")
                }
            }
        } else {
            val randomConfigParam: IrValueParameter? = randomConfigArgumentParamData?.first
            require(randomConfigParam?.hasDefaultValue() == true) {
                "impossible, a default class or object must be provided for @Randomizable, this is a mistake by the developer"
            }
            return getDefaultRandomConfigInstance(builder)
        }
    }

    /**
     * Construct an IrCall to access [RandomConfigImp.Companion.default]
     */
    private fun getDefaultRandomConfigInstance(builder: DeclarationIrBuilder): IrCall {
        return builder.irGetObject(defaultRandomConfigAccessor.defaultRandomConfigCompanionObject.symbol)
            .dotCall(builder.irCall(defaultRandomConfigAccessor.getDefaultRandomConfigInstance))
    }


    /**
     * Generate an [IrExpression] that can return a random instance of [irClass]
     */
    private fun generateRandomClassInstance(
        receivedTypeArguments: List<IrTypeArgument>?,
        param: IrValueParameter?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
    ): IrExpression? {

        val rt = stopAtFirstNotNull(
            { generateRandomObj(irClass, builder) },
            { generateRandomEnum(irClass, getRandomContextExpr, builder) },
            {
                generateRandomConcreteClass(
                    receivedTypeArgument = receivedTypeArguments,
                    paramFromConstructor = param,
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    typeParamOfRandomFunctionList = typeParamOfRandomFunction,
                    genericRandomFunctionParamList = genericRandomFunctionParamList,
                )
            },
            {
                generateRandomSealClass(
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction,
                    genericRandomFunctionParamList = genericRandomFunctionParamList,
                )
            },
            {
                generateRandomAbstractClass(
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction,
                    genericRandomFunctionParamList = genericRandomFunctionParamList,
                )
            }
        )
        return rt
    }

    private fun generateRandomObj(
        irClass: IrClass,
        builder: DeclarationIrBuilder,
    ): IrExpression? {
        if (irClass.isObject) {
            return builder.irGetObject(irClass.symbol)
        } else {
            return null
        }
    }

    private fun generateRandomEnum(
        irClass: IrClass,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression? {
        if (irClass.isEnumClass) {
            val getRandom = getRandomConfigExpr.dotCall(randomConfigAccessor.random(builder))
            if (irClass.hasEnumEntries) {
                // make an IR to access "entries"
                val irEntriesFunction = run {
                    val irEntries = irClass.declarations.firstOrNull {
                        it.getNameWithAssert().toString() == "entries"
                    } as? IrProperty
                    requireNotNull(irEntries?.getter) {
                        "enum ${irClass.name} does not have \"entries\" field"
                    }
                }

                // then call randomFunction on "entries" accessor ir
                val rt = builder.irCall(irEntriesFunction)
                    .extensionDotCall(builder.irCall(randomAccessor.randomFunctionOnCollectionOneArg))
                    .withValueArgs(getRandom)

                return rt
            } else {
                val irValues =
                    irClass.declarations.firstOrNull { it.getNameWithAssert().toString() == "values" } as? IrFunction

                if (irValues != null) {
                    val randomFunction = randomAccessor.randomFunctionOnArrayOneArg
                    val rt = builder.irCall(irValues)
                        .extensionDotCall(builder.irCall(randomFunction))
                        .withValueArgs(getRandom)
                    return rt
                }
            }

            throw IllegalArgumentException("Impossible - Enum ${irClass.name} does not have entries or values()")

        } else {
            return null
        }
    }

    /**
     * Concrete class is final or open class that is:
     * - not abstract
     * - not enum
     * - not object
     */
    private fun generateRandomConcreteClass(
        receivedTypeArgument: List<IrTypeArgument>?,
        /**
         * this param is the param from which [irClass] derived
         */
        paramFromConstructor: IrValueParameter?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunctionList: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
    ): IrExpression? {
        if (irClass.isFinalOrOpenConcrete() && !irClass.isObject && !irClass.isEnumClass) {
            val constructor = getConstructor(irClass)


            val typeArgumentList: List<IrTypeArgument> = stopAtFirstNotNull(
                { receivedTypeArgument },
                { (paramFromConstructor?.type as? IrSimpleType)?.arguments },
            ) ?: emptyList()

            val paramExpressions = constructor.valueParameters.withIndex().map { (index, param) ->
                generateRandomParam(
                    receivedTypeArgument = typeArgumentList.getOrNull(index),
                    paramFromConstructor = param,
                    builder = builder,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    typeParamOfRandomFunctionList = typeParamOfRandomFunctionList,
                    genericRandomFunctionParamList = genericRandomFunctionParamList,
                )
            }

            val constructorCall = builder.irCallConstructor(
                callee = constructor.symbol,
                typeArguments = emptyList()
            ).withValueArgs(paramExpressions)
            return constructorCall
        } else {
            return null
        }
    }

    private fun ifNotNullElse(
        type: IrType,
        canditate: IrExpression,
        onCandidateNotNull: IrExpression,
        onCandidateNull: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression {
        return builder.irIfNull(type, canditate, onCandidateNull, onCandidateNotNull)
    }

    /**
     * Construct an if-else expression using `RandomConfig.nextBool` as condition. Like this
     * ```
     * if(randomConfig.nextBool()){
     *    [randomPart]
     * }else{
     *    null << always return null on else
     * }
     * ```
     */
    private fun randomOrNull(
        builder: DeclarationIrBuilder,
        /**
         * An expr to get a [RandomConfig]
         */
        getRandomContext: IrExpression,
        /**
         * this is the return type of the if-else expr
         */
        type: IrType,
        randomPart: IrExpression,
    ): IrExpression {
        return randomIfElse(
            builder = builder,
            getRandomContextExpr = getRandomContext,
            type = type,
            truePart = randomPart,
            elsePart = builder.irNull()
        )
    }

    /**
     * Construct an if-else expression using `RandomConfig.nextBool` as condition. Like this
     * ```
     * if(randomConfig.nextBool()){
     *    [truePart]
     * }else{
     *    [elsePart]
     * }
     * ```
     */
    private fun randomIfElse(
        builder: DeclarationIrBuilder,
        /**
         * An expr to get a [RandomConfig]
         */
        getRandomContextExpr: IrExpression,
        /**
         * this is the return type of the if-else expr
         */
        type: IrType,
        truePart: IrExpression,
        elsePart: IrExpression
    ): IrExpression {
        val conditionExpr = getRandomContextExpr.dotCall(randomConfigAccessor.nextBoolean(builder))
        return builder.irIfThenElse(
            type = type,
            condition = conditionExpr,
            thenPart = truePart,
            elsePart = elsePart
        )
    }

    private fun generateRandomSealClass(
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
    ): IrExpression? {
        if (irClass.isSealed()) {
            TODO()
        } else {
            return null
        }
    }

    private fun generateRandomAbstractClass(
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
    ): IrExpression? {
        if (irClass.isAbstract() && !irClass.isSealed() && irClass.isAnnotatedWith(BaseObjects.randomizableFqName)) {
            TODO("not supported yet")
        } else {
            return null
        }
    }


    private fun generateRandomParam(
        /**
         * Received type argument is generic type information passed down from higher level to the param represented by [paramFromConstructor].
         */
        receivedTypeArgument: IrTypeArgument?,
        /**
         * parameter directly from a constructor.
         * In case of generic param, this object only contains the direct generic type from its constructor.
         * If there's a [receivedTypeArgument], then [receivedTypeArgument] it must be prioritized over this, because this one does not contain enough information to construct the correct call.
         */
        paramFromConstructor: IrValueParameter,
        builder: DeclarationIrBuilder,
        /**
         * An expression that return a [RandomContext]
         */
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        typeParamOfRandomFunctionList: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
        /**
         * get  random collection, this should return a reused variable, instead create anything new
         */
    ): IrExpression {
        val primitive = generatePrimitiveRandomParam(
            param = paramFromConstructor,
            builder = builder,
            getRandomContext = getRandomContextExpr,
            getRandomConfigExpr = getRandomConfigExpr,
        )

        if (primitive != null) {
            return primitive
        }
        val paramType = paramFromConstructor.type
        val constructorParamTypeSymbol = paramFromConstructor.type.classifierOrNull as? IrTypeParameterSymbol
        val receiveTypeIsGeneric = ((receivedTypeArgument as? IrSimpleType)?.classifierOrNull as? IrClassSymbol) == null

        val noReceivedType_Or_ReceivedTypeIsGeneric = receivedTypeArgument == null || receiveTypeIsGeneric

        if (paramType.isTypeParameter() && constructorParamTypeSymbol != null && noReceivedType_Or_ReceivedTypeIsGeneric) {

            val constructorParamTypeIndex = constructorParamTypeSymbol.owner.index

            val paramTypeIndex =
                ((receivedTypeArgument as? IrSimpleType)?.classifier as? IrTypeParameterSymbol)?.owner?.index
                    ?: constructorParamTypeIndex

            val paramTypeOfFunction = typeParamOfRandomFunctionList.getOrNull(paramTypeIndex)?.defaultType
            if (paramTypeOfFunction != null) {
                val nonNullRandom = builder.irBlock {
                    /**
                     * random from generic function
                     */
                    val randomFromGenericFunctionCall = run {
                        val lambdaFunction = builder.irGet(genericRandomFunctionParamList[paramTypeIndex])

                        lambdaFunction
                            .nullSafeDotCall(
                                irCall = function1Accessor.invokeFunction(builder),
                                returnType = paramTypeOfFunction,
                                builder = builder,
                                valueArgs = listOf(getRandomContextExpr),
                            )

                    }

                    val varRandomFromGenericFunction =
                        irTemporary(randomFromGenericFunctionCall, nameHint = "varRandomFromGenericFunction").apply {
                            type = paramTypeOfFunction.makeNullable()
                        }
                    val getVarRandomFromGenericFunction = irGet(varRandomFromGenericFunction)

                    /**
                     * Generate random for generic param under this order of priority:
                     * random context > generic factory function
                     */

                    val randomFromRandomContextCall = getRandomContextExpr
                        .extensionDotCall(randomContextAccessor.randomFunction(builder))
                        .withTypeArgs(paramTypeOfFunction)

                    +irIfNull(
                        type = paramTypeOfFunction,
                        subject = getVarRandomFromGenericFunction,
                        thenPart = randomFromRandomContextCall,
                        elsePart = getVarRandomFromGenericFunction,
                    )
                }
                val rt = if (paramType.isMarkedNullable()) {
                    randomOrNull(
                        builder = builder,
                        getRandomContext = getRandomContextExpr,
                        type = paramType,
                        randomPart = nonNullRandom,
                    )
                } else {
                    nonNullRandom
                }
                return rt
            } else {
                throw IllegalArgumentException("random() function does not have the same number of type param as the target class")
            }
        } else {

            val paramClass = run {
                val classFromReceivedTypeArg =
                    ((receivedTypeArgument as? IrSimpleType)?.classifierOrNull as? IrClassSymbol)?.owner
                classFromReceivedTypeArg ?: paramType.classOrNull?.owner
            }

            val passedAlongTypeArgument = run {
                val typeArgumentsFromReceivedType = (receivedTypeArgument as? IrSimpleType)?.arguments
                typeArgumentsFromReceivedType
            }

            if (paramClass != null) {
                /**
                 * random instance from randomizer level 0
                 */
                val randomInstance = generateRandomClassInstance(
                    receivedTypeArguments = passedAlongTypeArgument,
                    param = paramFromConstructor,
                    irClass = paramClass,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunctionList,
                    genericRandomFunctionParamList = genericRandomFunctionParamList,
                )

                if (randomInstance != null) {
                    val actualParamType = (receivedTypeArgument as? IrSimpleType) ?: paramType

                    val nonNullRandom = builder.irBlock {
                        /**
                         * random instance from random context
                         */
                        val randomFromRandomContextCall = getRandomContextExpr
                            .extensionDotCall(randomContextAccessor.randomFunction(builder))
                            .withTypeArgs(actualParamType)

                        val varRandomFromRandomContext =
                            irTemporary(randomFromRandomContextCall, "randomFromContext").apply {
                                this.type = actualParamType.makeNullable()
                            }

                        val getRandomFromRandomContext = irGet(varRandomFromRandomContext)

                        +irIfNull(
                            type = actualParamType,
                            subject = getRandomFromRandomContext,
                            thenPart = randomInstance,
                            elsePart = getRandomFromRandomContext
                        )
                    }

                    if (actualParamType.isNullable()) {
                       return randomOrNull(builder,getRandomConfigExpr,actualParamType,nonNullRandom)
                    } else {
                        return nonNullRandom
                    }
                } else {
                    throw IllegalArgumentException("unable to construct an expression to generate a random instance for ${paramFromConstructor.name}:${paramClass.name}")
                }
            } else {
                throw IllegalArgumentException("$paramFromConstructor does not belong to a class :| ")
            }
        }


    }

    /**
     * TODO add logic to pick a constructor:
     *  - prioritize annotated constructors
     *  - pick randomly
     */
    private fun getConstructor(targetClass: IrClass): IrConstructor {
        val primary = targetClass.primaryConstructor
        if (primary != null) {
            return primary
        } else {
            throw IllegalArgumentException("${targetClass.name} does not have a constructor")
        }
    }


    /**
     * Generate an [IrExpression] that will return a random value for a parameter ([param])
     */
    private fun generatePrimitiveRandomParam(
        param: IrValueParameter,
        builder: DeclarationIrBuilder,
        /**
         * An expression that return a [RandomConfig]
         */
        getRandomContext: IrExpression,
        getRandomConfigExpr: IrExpression,
    ): IrExpression? {

        if(true){
            val paramType = param.type
            val randomCallForRandomConfigCall = if (paramType.isNullable()) {
                when {
                    paramType.isInt2(true) -> randomConfigAccessor.nextInt(builder)
                    paramType.isUInt2(true) -> randomConfigAccessor.nextUInt(builder)
                    paramType.isLong2(true) -> randomConfigAccessor.nextLong(builder)
                    paramType.isULong2(true) -> randomConfigAccessor.nextULong(builder)
                    paramType.isByte2(true) -> randomConfigAccessor.nextByte(builder)
                    paramType.isUByte2(true) -> randomConfigAccessor.nextUByte(builder)
                    paramType.isShort2(true) -> randomConfigAccessor.nextShort(builder)
                    paramType.isUShort2(true) -> randomConfigAccessor.nextUShort(builder)
                    paramType.isBoolean2(true) -> randomConfigAccessor.nextBoolean(builder)
                    paramType.isFloat2(true) -> randomConfigAccessor.nextFloat(builder)
                    paramType.isDouble2(true) -> randomConfigAccessor.nextDouble(builder)
                    paramType.isChar2(true) -> randomConfigAccessor.nextChar(builder)
                    paramType.isString2(true) -> randomConfigAccessor.nextStringUUID(builder)
                    paramType.isUnit2(true) -> randomConfigAccessor.nextUnit(builder)
                    paramType.isNumber2(true) -> randomConfigAccessor.nextNumber(builder)
                    paramType.isAny2(true) -> randomConfigAccessor.nextAny(builder)
                    paramType.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")
                    else -> null
                }
            } else {
                when {
                    paramType.isInt2(false) -> (randomConfigAccessor.nextInt(builder))
                    paramType.isUInt2(false) -> (randomConfigAccessor.nextUInt(builder))
                    paramType.isLong2(false) -> (randomConfigAccessor.nextLong(builder))
                    paramType.isULong2(false) -> (randomConfigAccessor.nextULong(builder))
                    paramType.isByte2(false) -> (randomConfigAccessor.nextByte(builder))
                    paramType.isUByte2(false) -> (randomConfigAccessor.nextUByte(builder))
                    paramType.isShort2(false) -> (randomConfigAccessor.nextShort(builder))
                    paramType.isUShort2(false) -> (randomConfigAccessor.nextUShort(builder))
                    paramType.isBoolean2(false) -> (randomConfigAccessor.nextBoolean(builder))
                    paramType.isFloat2(false) -> (randomConfigAccessor.nextFloat(builder))
                    paramType.isDouble2(false) -> (randomConfigAccessor.nextDouble(builder))
                    paramType.isChar2(false) -> (randomConfigAccessor.nextChar(builder))
                    paramType.isString2(false) -> (randomConfigAccessor.nextStringUUID(builder))
                    paramType.isUnit2(false) -> (randomConfigAccessor.nextUnit(builder))
                    paramType.isNumber2(false) -> (randomConfigAccessor.nextNumber(builder))
                    paramType.isAny2(false) -> (randomConfigAccessor.nextAny(builder))
                    paramType.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")
                    else -> null
                }
            }
            return randomCallForRandomConfigCall?.let {
                randomFromRandomContextOrRandomConfig2(
                    type = paramType,
                    getRandomContext = getRandomContext,
                    getRandomConfigExpr = getRandomConfigExpr,
                    randomConfigRandomExpr = getRandomConfigExpr.dotCall(randomCallForRandomConfigCall),
                    builder = builder,
                )
            }
        }else{
            val paramType = param.type
            val randomCallForRandomConfig = if (paramType.isNullable()) {
                when {
                    paramType.isInt2(true) -> randomConfigAccessor.nextIntOrNull(builder)
                    paramType.isUInt2(true) -> randomConfigAccessor.nextUIntOrNull(builder)
                    paramType.isLong2(true) -> randomConfigAccessor.nextLongOrNull(builder)
                    paramType.isULong2(true) -> randomConfigAccessor.nextULongOrNull(builder)
                    paramType.isByte2(true) -> randomConfigAccessor.nextByteOrNull(builder)
                    paramType.isUByte2(true) -> randomConfigAccessor.nextUByteOrNull(builder)
                    paramType.isShort2(true) -> randomConfigAccessor.nextShortOrNull(builder)
                    paramType.isUShort2(true) -> randomConfigAccessor.nextUShortOrNull(builder)
                    paramType.isBoolean2(true) -> randomConfigAccessor.nextBoolOrNull(builder)
                    paramType.isFloat2(true) -> randomConfigAccessor.nextFloatOrNull(builder)
                    paramType.isDouble2(true) -> randomConfigAccessor.nextDoubleOrNull(builder)
                    paramType.isChar2(true) -> randomConfigAccessor.nextCharOrNull(builder)
                    paramType.isString2(true) -> randomConfigAccessor.nextStringUUIDOrNull(builder)
                    paramType.isUnit2(true) -> randomConfigAccessor.nextUnitOrNull(builder)
                    paramType.isNumber2(true) -> randomConfigAccessor.nextNumberOrNull(builder)
                    paramType.isAny2(true) -> randomConfigAccessor.nextAnyOrNull(builder)
                    paramType.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")
                    else -> null
                }
            } else {
                when {
                    paramType.isInt2(false) -> (randomConfigAccessor.nextInt(builder))
                    paramType.isUInt2(false) -> (randomConfigAccessor.nextUInt(builder))
                    paramType.isLong2(false) -> (randomConfigAccessor.nextLong(builder))
                    paramType.isULong2(false) -> (randomConfigAccessor.nextULong(builder))
                    paramType.isByte2(false) -> (randomConfigAccessor.nextByte(builder))
                    paramType.isUByte2(false) -> (randomConfigAccessor.nextUByte(builder))
                    paramType.isShort2(false) -> (randomConfigAccessor.nextShort(builder))
                    paramType.isUShort2(false) -> (randomConfigAccessor.nextUShort(builder))
                    paramType.isBoolean2(false) -> (randomConfigAccessor.nextBoolean(builder))
                    paramType.isFloat2(false) -> (randomConfigAccessor.nextFloat(builder))
                    paramType.isDouble2(false) -> (randomConfigAccessor.nextDouble(builder))
                    paramType.isChar2(false) -> (randomConfigAccessor.nextChar(builder))
                    paramType.isString2(false) -> (randomConfigAccessor.nextStringUUID(builder))
                    paramType.isUnit2(false) -> (randomConfigAccessor.nextUnit(builder))
                    paramType.isNumber2(false) -> (randomConfigAccessor.nextNumber(builder))
                    paramType.isAny2(false) -> (randomConfigAccessor.nextAny(builder))
                    paramType.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")
                    else -> null
                }
            }
            return randomCallForRandomConfig?.let {
                randomFromRandomContextOrRandomConfig(
                    type = paramType,
                    getRandomContext = getRandomContext,
                    getRandomConfigExpr = getRandomConfigExpr,
                    randomConfigRandomCall = randomCallForRandomConfig,
                    builder = builder,
                )
            }
        }
    }

    private fun randomFromRandomContextOrRandomConfig(
        type: IrType,
        getRandomContext: IrExpression,
        getRandomConfigExpr: IrExpression,
        randomConfigRandomCall: IrCall,
        builder: DeclarationIrBuilder
    ): IrExpression {
        return evaluateRandomContextThenRandomConfig(
            type = type,
            randomFromRandomContext = getRandomContext
                .extensionDotCall(randomContextAccessor.randomFunction(builder))
                .withTypeArgs(type),
            randomFromRandomConfig = getRandomConfigExpr.dotCall(randomConfigRandomCall),
            builder = builder,
        )
    }

    private fun randomFromRandomContextOrRandomConfig2(
        type: IrType,
        getRandomContext: IrExpression,
        getRandomConfigExpr: IrExpression,
        randomConfigRandomExpr: IrExpression,
        builder: DeclarationIrBuilder
    ): IrExpression {

        val nonNullExpr = evaluateRandomContextThenRandomConfig(
            type = type,
            randomFromRandomContext = getRandomContext
                .extensionDotCall(randomContextAccessor.randomFunction(builder))
                .withTypeArgs(type),
            randomFromRandomConfig = randomConfigRandomExpr,
            builder = builder,
        )
        if(type.isNullable()){
            return randomOrNull(builder,getRandomContext,type,nonNullExpr)
        }else{
            return nonNullExpr
        }


    }

    private fun evaluateRandomContextThenRandomConfig(
        type: IrType,
        randomFromRandomContext: IrExpression,
        randomFromRandomConfig: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression {
        return builder.irBlock {

            val randomFromContextVar = irTemporary(randomFromRandomContext,"randomFromContextVar")
            val getRandomFromContextVar = irGet(randomFromContextVar)

            +builder.irIfNull(
                type = type,
                subject = getRandomFromContextVar,
                thenPart = randomFromRandomConfig,
                elsePart = getRandomFromContextVar
            )
        }
    }


    private fun irPrintln(
        prefix: String,
        builder: IrBuilderWithScope,
        irExpr: IrExpression,
    ): IrCall {

        val typeAnyOrNull = pluginContext.irBuiltIns.anyNType
        val printlnFunction = pluginContext.referenceFunctions(BaseObjects.printlnCallId).firstOrNull {
            it.owner.valueParameters.let {
                it.size == 1 && it[0].type == typeAnyOrNull
            }
        }


        val strContent = builder.irConcat().apply {
            addArgument(builder.irString("$prefix:"))
            addArgument(irExpr)
        }

        val printlnCall = printlnFunction?.let {
            builder.irCall(it).apply {
                this.putValueArgument(0, strContent)
            }
        }

        return printlnCall!!
    }

}
