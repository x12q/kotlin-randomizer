package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.lib.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.backend.transformers.accessor.*
import com.x12q.randomizer.ir_plugin.backend.utils.*
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.runSideEffect
import com.x12q.randomizer.ir_plugin.util.stopAtFirstNotNull
import com.x12q.randomizer.lib.*
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addExtensionReceiver
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrThrowImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import javax.inject.Inject

/**
 * Order of priority: random context > generic factory function
 */
class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
    private val randomAccessor: RandomAccessor,
    private val randomConfigAccessor: RandomConfigAccessor,
    private val defaultRandomConfigAccessor: DefaultRandomConfigAccessor,
    private val function0Accessor: Function0Accessor,
    private val function1Accessor: Function1Accessor,
    private val randomContextBuilderAccessor: RandomizerContextBuilderAccessor,
    private val randomContextBuilderImpAccessor: RandomContextBuilderImpAccessor,
    private val randomizerCollectionAccessor: RandomizerCollectionAccessor,
    private val randomContextAccessor: RandomContextAccessor,
    private val unableToMakeRandomExceptionAccessor: UnableToMakeRandomExceptionAccessor,
    private val classRandomizerAccessor: ClassRandomizerAccessor,
    private val classRandomizerUtilAccessor: ClassRandomizerUtilAccessor,
    private val listAccessor: ListAccessor,
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


    private fun isGeneratedRandomFunction(function: IrSimpleFunction): Boolean {
        val origin = function.origin
        val isGeneratedByRandomizerPlugin =
            (origin is IrDeclarationOrigin.GeneratedByPlugin) && (origin.pluginKey == BaseObjects.randomizableDeclarationKey)
        val nameIsRandom = function.name == BaseObjects.randomFunctionName
        return isGeneratedByRandomizerPlugin && nameIsRandom
    }

    /**
     * The purpose of this transformation is:
     * - add randomizers for generic type at call side of random() functions
     *
     * Explanation:
     * - Only at call side that the concrete type of generic type is known.
     * - Therefore, only at call side there's enough information to generate randomizers for generic type.
     */
    override fun visitCall(expression: IrCall): IrExpression {

        val function = expression.symbol.owner

        if (isGeneratedRandomFunction(function)) {

            val randomFunction = function

            val randomizersParam = function.valueParameters.firstOrNull {
                it.name == BaseObjects.randomContextBuilderConfigFunctionParamName
            }
            if (randomizersParam != null) {
                val providedRandomizersArgument = expression.getValueArgument(randomizersParam.index)

                /**
                 * Extract the randomizers lambda, use the default if none is available
                 * this lambda signature is: randomizers = RandomContextBuilder.{}
                 */
                val randomizersLambda = if (providedRandomizersArgument != null) {
                    (providedRandomizersArgument as? IrFunctionExpression)?.function
                } else {
                    val defaultRandomizers =
                        (randomizersParam.defaultValue?.expression as? IrFunctionExpression)?.function
                    val newDefault = cloneDefaultRandomizersArgument(defaultRandomizers)
                    runSideEffect {
                        // replace the default argument with a copy of it
                        val newDefaultArg = newDefault?.let {
                            IrFunctionExpressionImpl(
                                startOffset = newDefault.startOffset,
                                endOffset = newDefault.endOffset,
                                type = pluginContext.irBuiltIns.functionN(1)
                                    .typeWith(randomContextBuilderAccessor.irType, newDefault.returnType),
                                function = newDefault,
                                origin = IrStatementOrigin.LAMBDA
                            )
                        }
                        expression.putValueArgument(randomizersParam.index, newDefaultArg)
                    }
                    newDefault
                }

                if (randomizersLambda != null) {

                    val builder = DeclarationIrBuilder(
                        generatorContext = pluginContext,
                        symbol = randomizersLambda.symbol,
                    )

                    val newBody = builder.irBlockBody {
                        val blockBodyBuilder = this
                        val randomContextBuilder = requireNotNull(randomizersLambda.extensionReceiverParameter) {
                            "at this point, it must be guaranteed that a valid RandomContext is passed to randomizers lambda"
                        }

                        for (typeArg in expression.typeArguments) {

                            if (typeArg != null && !typeArg.isProvidedPrimitive(typeArg.isNullable())) {

                                /**
                                 * Explain the condition of this block:
                                 * Randomizers are generated only for:
                                 * - type that is not null
                                 * - type that is NOT primitives because primitives already have built-in randomizers.
                                 */

                                /**
                                 * This call "addForTier2" function inside "randomizers" lambda
                                 */
                                val addForTier2_FunctionCall = irGet(randomContextBuilder)
                                    .dotCall(randomContextBuilderAccessor.addForTier2Call(blockBodyBuilder))

                                /**
                                 * This lambda is: RandomContext.() -> ClassRandomizer<*>
                                 */
                                val makeRandomizerLambda = generate_makeClassRandomizer_Lambda(
                                    randomizersLambda = randomizersLambda,
                                    randomType = typeArg,
                                    typeParamOfRandomFunction = randomFunction.typeParameters,
                                )

                                addForTier2_FunctionCall.putValueArgument(
                                    index = 0,
                                    valueArgument = IrFunctionExpressionImpl(
                                        startOffset = makeRandomizerLambda.startOffset,
                                        endOffset = makeRandomizerLambda.endOffset,
                                        type = pluginContext.irBuiltIns.functionN(1)
                                            .typeWith(
                                                randomContextAccessor.irType,
                                                makeRandomizerLambda.returnType,
                                            ),
                                        function = makeRandomizerLambda,
                                        origin = IrStatementOrigin.LAMBDA,
                                    )
                                )
                                +addForTier2_FunctionCall
                            }
                        }

                        /**
                         * Appended old body's statements into the end of the new body
                         */
                        randomizersLambda.body?.statements?.forEach { statement ->
                            +statement
                        }
                    }
                    randomizersLambda.body = newBody
                }
            }

        }
        return super.visitCall(expression)
    }

    // randomizers = RandomContextBuilder.{}
    private fun cloneDefaultRandomizersArgument(originalDefault: IrSimpleFunction?): IrSimpleFunction? {
        if (originalDefault != null) {
            val rt = pluginContext.irFactory.buildFun {
                // updateFrom(originalDefault)
                name = SpecialNames.ANONYMOUS
                origin = originalDefault.origin
                visibility = originalDefault.visibility
                returnType = originalDefault.returnType
                modality = FINAL
                isSuspend = false
            }.apply {
                parent = originalDefault.parent
                originalDefault.extensionReceiverParameter?.type?.also {
                    addExtensionReceiver(it)
                }
                val builder = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = this.symbol,
                )
                body = builder.irBlockBody {
                    originalDefault.body?.statements?.forEach { stm ->
                        +stm
                    }
                }
            }
            return rt
        } else {
            return null
        }
    }

    /**
     * This build a lambda passed to "addForTier2"
     * This lambda accept a RandomContext as its extension param, and return a ClassRandomizer<*>
     * Like this
     *     RandomContext.() -> ClassRandomizer<*>
     */
    private fun generate_makeClassRandomizer_Lambda(
        randomizersLambda: IrDeclarationParent,
        randomType: IrType,
        typeParamOfRandomFunction: List<IrTypeParameter>,
    ): IrSimpleFunction {

        val generatedLambda = pluginContext.irFactory.buildFun {
            name = SpecialNames.ANONYMOUS
            origin = BaseObjects.declarationOrigin
            visibility = DescriptorVisibilities.LOCAL
            returnType = classRandomizerAccessor.clzz.starProjectedType
            modality = FINAL
            isSuspend = false
        }.apply {

            parent = randomizersLambda

            val makeClassRandomizerLambda = this
            makeClassRandomizerLambda.addExtensionReceiver(randomContextAccessor.irType)

            val builder = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = makeClassRandomizerLambda.symbol,
            )

            val newBody = builder.irBlockBody {
                val randomContextParam = requireNotNull(makeClassRandomizerLambda.extensionReceiverParameter)
                val getRandomContextExpr = irGet(randomContextParam)

                val rdType = requireNotNull(randomType as? IrSimpleType)

                /**
                 * This lambda:
                 * - generates a random instance of [randomType].
                 * - is passed to "factoryRandomizer" function
                 */
                val makeRandomInstanceLambda = generate_makeRandom_Lambda(
                    declarationParent = makeClassRandomizerLambda,
                    randomTargetType = rdType,
                    typeParamOfRandomFunction = typeParamOfRandomFunction,
                    getRandomContextExpr = getRandomContextExpr,
                )

                val classRandomizerMakingCall =
                    classRandomizerUtilAccessor.factoryClassRandomizerFunctionCall(builder).apply {
                        putValueArgument(
                            index = 0,
                            valueArgument = IrFunctionExpressionImpl(
                                startOffset = makeRandomInstanceLambda.startOffset,
                                endOffset = makeRandomInstanceLambda.endOffset,
                                type = pluginContext.irBuiltIns.functionN(0)
                                    .typeWith(makeRandomInstanceLambda.returnType),
                                function = makeRandomInstanceLambda,
                                origin = IrStatementOrigin.LAMBDA
                            )
                        )
                        putTypeArgument(0, rdType)
                    }

                +irReturn(classRandomizerMakingCall)
            }
            body = newBody
        }

        val rt = generatedLambda
        return rt
    }


    /**
     * this generates the lambda passed to factoryRandomizer, like this: factoryRandomizer(makeRandom = factoryLambda <~ this one)
     * This factoryLambda return a random instance of [randomTargetType]
     */
    private fun generate_makeRandom_Lambda(
        declarationParent: IrDeclarationParent,
        randomTargetType: IrSimpleType,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        getRandomContextExpr: IrExpression,
    ): IrSimpleFunction {

        val clzz = randomTargetType.classOrNull
        requireNotNull(clzz) {
            "generate_factoryLambda only works with concrete class"
        }
        require(!clzz.owner.isAbstract()) {
            "generate_factoryLambda only works with concrete class"
        }

        val generatedLambda = pluginContext.irFactory.buildFun {
            name = SpecialNames.ANONYMOUS
            // name = Name.special("<makeRandom>")
            origin = BaseObjects.declarationOrigin
            visibility = DescriptorVisibilities.LOCAL
            returnType = randomTargetType
            modality = FINAL
            isSuspend = false
        }.apply {
            parent = declarationParent

            val function = this

            val innerBuilder = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = function.symbol,
            )

            /**
             * The body constructs a random instance of [randomTargetType]
             */
            val newBody = innerBuilder.irBlockBody {
                +irReturn(
                    irBlock {
                        val getRandomContext = if (false) {
                            val randomContextParam = requireNotNull(function.extensionReceiverParameter)
                            irGet(randomContextParam)
                        } else {
                            getRandomContextExpr
                        }
                        val generateExpr = generateRandomClass(
                            receivedTypeArguments = randomTargetType.arguments,
                            param = null,
                            irClass = clzz.owner,
                            getRandomContextExpr = getRandomContext,
                            getRandomConfigExpr = getRandomContext,
                            builder = innerBuilder,
                            typeParamOfRandomFunction = typeParamOfRandomFunction,
                        )
                        if (generateExpr != null) {
                            +generateExpr
                        }
                    }
                )
            }
            body = newBody
        }

        val rt = generatedLambda
        return rt
    }

    /**
     * complete random() function in [companionObj].
     * This function use the random config in annotation.
     *
     * ```
     *      fun random(
     *          randomizers:RandomContextBuilder.()->Unit = {},
     *      )
     * ```
     */
    private fun completeRandomFunction1(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            val con1 = function.name == BaseObjects.randomFunctionName
            val con2 = function.valueParameters.size == 1
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
            )
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

        val constructorCall = generateRandomClass(
            receivedTypeArguments = emptyList(),
            param = null,
            irClass = target,
            getRandomContextExpr = getRandomContext,
            getRandomConfigExpr = getRandomConfigFromVar,
            builder = builder,
            typeParamOfRandomFunction = typeParamOfRandomFunction,
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
                randomContextBuilderAccessor.setRandomConfigAndGenerateStandardRandomizersFunction(builder)
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
        return getRandomContextBuilderExpr.dotCall(randomContextBuilderAccessor.buildRandomConfigFunction(builder))
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
                function.name == BaseObjects.randomFunctionName && function.valueParameters.size == 2
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
    private fun generateRandomClass(
        receivedTypeArguments: List<IrTypeArgument>?,
        param: IrValueParameter?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
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
                )
            },
            {
                generateStdCollection(
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction
                )
            },
            {
                generateRandomSealClass(
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction,
                )
            },
            {
                generateRandomAbstractClass(
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction,
                )
            }
        )
        return rt
    }

    fun generateStdCollection(
        receivedTypeArguments: List<IrTypeArgument>?,
        param: IrValueParameter?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
    ): IrExpression? {
        val rt = stopAtFirstNotNull(
            {
                generateList_withinRandomFunction(
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction
                )
            },
            {
                generateMap(
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction
                )
            },
            {
                generateSet(
                    receivedTypeArguments = receivedTypeArguments,
                    param = param,
                    irClass = irClass,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction
                )
            }

        )
        return rt
    }


    fun generateList_withinRandomFunction(
        receivedTypeArguments: List<IrTypeArgument>?,
        param: IrValueParameter?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
    ): IrExpression? {
        if (!irClass.isList()) {
            return null
        }
        // get element type
        val elementTypes = extractTypeArgument(receivedTypeArguments, param).firstOrNull()
        if(elementTypes!=null){
            // create expression to construct random instances of elements


            // create expression to call List() function and random config
        }else{
            // TODO consider throw an exception here because can't find a type param for a kotlin.List
            return null
        }
        return null
    }

    fun generateMap(
        receivedTypeArguments: List<IrTypeArgument>?,
        param: IrValueParameter?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
    ): IrExpression? {
        // TODO
        return null
    }

    fun generateSet(
        receivedTypeArguments: List<IrTypeArgument>?,
        param: IrValueParameter?,
        irClass: IrClass,
        getRandomContextExpr: IrExpression,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
    ): IrExpression? {
        // TODO
        return null
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
    ): IrExpression? {
        if (irClass.isFinalOrOpenConcrete() && !irClass.isObject && !irClass.isEnumClass) {
            val constructor = getConstructor(irClass)

            val typeArgumentList: List<IrTypeArgument> = extractTypeArgument(
                receivedTypeArgument, paramFromConstructor
            )

            val paramExpressions = constructor.valueParameters.withIndex().map { (index, param) ->

                val typeIndex = (param.type.classifierOrNull as? IrTypeParameterSymbol)?.owner?.index
                generateRandomParam(
                    receivedTypeArgument = typeIndex?.let { typeArgumentList.getOrNull(it) },
                    paramFromConstructor = param,
                    builder = builder,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    typeParamOfRandomFunctionList = typeParamOfRandomFunctionList,
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

    private fun extractTypeArgument(
        receivedTypeArgument: List<IrTypeArgument>?,
        paramFromConstructor: IrValueParameter?,
    ): List<IrTypeArgument> {
        val rt: List<IrTypeArgument> = stopAtFirstNotNull(
            { receivedTypeArgument },
            { (paramFromConstructor?.type as? IrSimpleType)?.arguments },
        ) ?: emptyList()
        return rt
    }

    private fun ifNotNullElse(
        type: IrType,
        candidate: IrExpression,
        onCandidateNotNull: IrExpression,
        onCandidateNull: IrExpression,
        builder: DeclarationIrBuilder,
    ): IrExpression {
        return builder.irIfNull(type, candidate, onCandidateNull, onCandidateNotNull)
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
    ): IrExpression {
        val primitive = generatePrimitiveRandomParam(
            receivedTypeArgument = receivedTypeArgument,
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
        val receivedType = receivedTypeArgument as? IrSimpleType
        val receivedTypeClassifier = receivedType?.classifierOrNull
        val receiveTypeIsGeneric = receivedTypeClassifier !is IrClassSymbol
        val `dont receive type or received type is generic` = receivedTypeArgument == null || receiveTypeIsGeneric

        if (paramType.isTypeParameter()
            && constructorParamTypeSymbol != null
            && `dont receive type or received type is generic`
        ) {

            requireNotNull(constructorParamTypeSymbol) { "impossible for constructorParamTypeSymbol to be null at this point" }

            val paramTypeIndex = (receivedTypeClassifier as? IrTypeParameterSymbol)?.owner?.index
                ?: constructorParamTypeSymbol.owner.index

            val paramTypeOfFunction = typeParamOfRandomFunctionList.getOrNull(paramTypeIndex)?.defaultType

            requireNotNull(paramTypeOfFunction) {
                "Can't find concrete type for param ${paramFromConstructor.dumpKotlinLike()}. This is most likely a bug by the developer."
            }

            val nonNullRandom = getRandomContextExpr
                .extensionDotCall(randomContextAccessor.randomFunction(builder))
                .withTypeArgs(paramTypeOfFunction)

            val rt = if (paramType.isMarkedNullable()) {
                randomOrNull(
                    builder = builder,
                    getRandomContext = getRandomContextExpr,
                    type = paramType,
                    randomPart = nonNullRandom,
                )
            } else {
                return randomOrThrow(
                    builder,
                    nonNullRandom,
                    paramType,
                    paramFromConstructor.name.asString(),
                    paramFromConstructor.parentClassOrNull?.name?.asString() ?: ""
                )
            }
            return rt

        } else {
            /**
             * This else block handles all the rest cases in which:
             * - param type can be generic or not
             * - and constructor is invoked using generic type provided from outside, not the type from the constructor.
             */

            /**
             * Get param class either from:
             * - or from received type (1st choice)
             * - directly from param type
             */


            val paramClass = receivedType?.classOrNull?.owner
                ?: paramType.classOrNull?.owner

            if (paramClass != null) {

                val randomInstanceExpr = generateRandomClass(
                    receivedTypeArguments = receivedType?.arguments,
                    param = paramFromConstructor,
                    irClass = paramClass,
                    getRandomContextExpr = getRandomContextExpr,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunctionList,
                )

                if (randomInstanceExpr != null) {
                    val actualParamType = receivedType ?: paramType

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
                            thenPart = randomInstanceExpr,
                            elsePart = getRandomFromRandomContext
                        )
                    }

                    if (actualParamType.isNullable()) {
                        return randomOrNull(builder, getRandomConfigExpr, actualParamType, nonNullRandom)
                    } else {
                        return randomOrThrow(
                            builder,
                            nonNullRandom,
                            actualParamType,
                            paramFromConstructor.name.asString(),
                            paramFromConstructor.parentClassOrNull?.name?.asString() ?: ""
                        )
                    }
                } else {
                    throw IllegalArgumentException("unable to construct an expression to generate a random instance for ${paramFromConstructor.name}:${paramClass.name}")
                }
            } else {
                throw IllegalArgumentException("$paramFromConstructor does not belong to a class.")
            }
        }
    }


    /**
     * Construct an express that:
     * - either return whatever [randomExpr] returns if such value is not null
     * - or throw an exception at runtime
     */
    private fun randomOrThrow(
        builder: DeclarationIrBuilder,
        randomExpr: IrExpression,
        type: IrType,
        paramName: String,
        enclosingClassName: String,
    ): IrExpression {
        return builder.irBlock {
            val randomResult = irTemporary(randomExpr, "randomResult")
            val getRandomResult = irGet(randomResult)
            val throwExceptionExpr = throwUnableToRandomizeException(
                builder = this,
                paramName = paramName,
                typeName = type.dumpKotlinLike(),
                enclosingClassName = enclosingClassName
            )
            +irIfNull(type, getRandomResult, throwExceptionExpr, getRandomResult)
        }
    }

    /**
     * Construct and throw an instance of [UnableToMakeRandomException]
     */
    private fun throwUnableToRandomizeException(
        builder: IrBuilderWithScope,
        paramName: String,
        typeName: String,
        enclosingClassName: String
    ): IrThrowImpl {

        return with(builder) {
            irThrow(
                irCallConstructor(
                    callee = unableToMakeRandomExceptionAccessor.primaryConstructor().symbol,
                    typeArguments = emptyList()
                ).withValueArgs(
                    /*targetClassName:String*/ irString(enclosingClassName),
                    /*paramName:String*/ irString(paramName),
                    /*paramType:String*/ irString(typeName)
                )
            )
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
        receivedTypeArgument: IrTypeArgument?,
        param: IrValueParameter,
        builder: DeclarationIrBuilder,
        /**
         * An expression that return a [RandomConfig]
         */
        getRandomContext: IrExpression,
        getRandomConfigExpr: IrExpression,
    ): IrExpression? {
        val paramType = receivedTypeArgument?.typeOrNull ?: param.type
        return generateRandomPrimitive(paramType,builder, getRandomContext, getRandomConfigExpr)
    }

    /**
     * Generate an [IrExpression] that will return a random value for a parameter ([param])
     */
    private fun generateRandomPrimitive(
        type:IrType,
        builder: DeclarationIrBuilder,
        /**
         * An expression that return a [RandomConfig]
         */
        getRandomContext: IrExpression,
        getRandomConfigExpr: IrExpression,
    ): IrExpression? {
        val isNullable = type.isNullable()
        val randomCallForRandomConfigCall = when {
            type.isInt2(isNullable) -> randomConfigAccessor.nextInt(builder)
            type.isUInt2(isNullable) -> randomConfigAccessor.nextUInt(builder)
            type.isLong2(isNullable) -> randomConfigAccessor.nextLong(builder)
            type.isULong2(isNullable) -> randomConfigAccessor.nextULong(builder)
            type.isByte2(isNullable) -> randomConfigAccessor.nextByte(builder)
            type.isUByte2(isNullable) -> randomConfigAccessor.nextUByte(builder)
            type.isShort2(isNullable) -> randomConfigAccessor.nextShort(builder)
            type.isUShort2(isNullable) -> randomConfigAccessor.nextUShort(builder)
            type.isBoolean2(isNullable) -> randomConfigAccessor.nextBoolean(builder)
            type.isFloat2(isNullable) -> randomConfigAccessor.nextFloat(builder)
            type.isDouble2(isNullable) -> randomConfigAccessor.nextDouble(builder)
            type.isChar2(isNullable) -> randomConfigAccessor.nextChar(builder)
            type.isString2(isNullable) -> randomConfigAccessor.nextStringUUID(builder)
            type.isUnit2(isNullable) -> randomConfigAccessor.nextUnit(builder)
            type.isNumber2(isNullable) -> randomConfigAccessor.nextNumber(builder)
            type.isAny2(isNullable) -> randomConfigAccessor.nextAny(builder)
            type.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")
            else -> null
        }

        return randomCallForRandomConfigCall?.let {
            randomFromRandomContextOrRandomConfig(
                type = type,
                getRandomContext = getRandomContext,
                randomFromConfigRandomExpr = getRandomConfigExpr.dotCall(randomCallForRandomConfigCall),
                builder = builder,
            )
        }
    }



    private fun randomFromRandomContextOrRandomConfig(
        type: IrType,
        getRandomContext: IrExpression,
        /**
         * [randomFromConfigRandomExpr] return a random instance of [type]
         */
        randomFromConfigRandomExpr: IrExpression,
        builder: DeclarationIrBuilder
    ): IrExpression {

        val nonNullExpr = evaluateRandomContextThenRandomConfig(
            type = type,
            randomFromRandomContext = getRandomContext
                .extensionDotCall(randomContextAccessor.randomFunction(builder))
                .withTypeArgs(type),
            randomFromRandomConfig = randomFromConfigRandomExpr,
            builder = builder,
        )
        if (type.isNullable()) {
            return randomOrNull(builder, getRandomContext, type, nonNullExpr)
        } else {
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

            val randomFromContextVar = irTemporary(randomFromRandomContext, "randomFromContextVar")
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
