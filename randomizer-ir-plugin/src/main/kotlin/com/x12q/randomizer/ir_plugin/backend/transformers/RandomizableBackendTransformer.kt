package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.lib.DefaultRandomConfig
import com.x12q.randomizer.lib.RandomConfig
import com.x12q.randomizer.lib.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.*
import com.x12q.randomizer.ir_plugin.backend.utils.*
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.stopAtFirstNotNullResult
import com.x12q.randomizer.lib.RandomizerCollection
import com.x12q.randomizer.lib.RandomizerCollectionBuilder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

@OptIn(UnsafeDuringIrConstructionAPI::class)
class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
    private val randomAccessor: RandomAccessor,
    private val randomConfigAccessor: RandomConfigAccessor,
    private val defaultRandomConfigAccessor: DefaultRandomConfigAccessor,
    private val function0Accessor: Function0Accessor,
    private val function1Accessor: Function1Accessor,
    private val randomizerCollectionBuilderAccessor: RandomizerCollectionBuilderAccessor,
    private val randomizerCollectionBuilderImpAccessor: RandomizerCollectionBuilderImpAccessor,
    private val randomizerCollectionAccessor: RandomizerCollectionAccessor
) : RDBackendTransformer() {

    val origin = BaseObjects.declarationOrigin

    override fun visitClassNew(declaration: IrClass): IrStatement {

        val annotation = declaration.getAnnotation(BaseObjects.randomizableFqName)
        if (annotation != null) {
            val companionObj = declaration.companionObject()
            if (companionObj != null) {
                completeRandomFunction1(companionObj, declaration)
                completeRandomFunction2(companionObj, declaration)
//                completeRandomizerFunction(companionObj, declaration)
            }
        }
        return super.visitClassNew(declaration)
    }

    /**
     * A Randomizer function is one that return an instance of randomizer declared inside the annotation
     */
    private fun completeRandomizerFunction(companionObj: IrClass, target: IrClass) {
        val randomizerFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            function.name == BaseObjects.randomizerFunctionName
        }

        if (randomizerFunction != null) {
            val annotation = target.getAnnotation(BaseObjects.randomizableFqName)
            if (annotation != null) {
                val builder = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = randomizerFunction.symbol,
                )
                val randomConfigExpression = makeGetRandomConfigExpressionFromAnnotation(annotation, builder)

                val createRandomizerExpression = generateRandomClassInstance(
                    irClass = target,
                    getRandomConfigExpr = randomConfigExpression,
                    builder = builder,
                    typeParamOfRandomFunction = emptyList(),
                    genericRandomFunctionParamList = emptyList(),
                    getRandomizerCollection = TODO()
                )
                if (createRandomizerExpression != null) {
                    randomizerFunction.body = builder.irBlockBody {
                        //return a new instance of randomizer
                    }
                } else {
                    throw IllegalArgumentException("unable generate constructor call")
                }
            }
        }
    }


    /**
     * complete random() function in [companionObj].
     * This function use the random config in annotation.
     *
     * ```
     *      fun random(randomizers:ClassRandomizerCollectionBuilder.()->Unit = {})
     * ```
     */
    private fun completeRandomFunction1(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            val con1 = function.name == BaseObjects.randomFunctionName
            val con2 = function.valueParameters.size == target.typeParameters.size + 1
            val con3 = function.valueParameters.lastOrNull()?.let { firstParam ->
                firstParam.name == BaseObjects.randomizersBuilderParamName
            } ?: false
            con1 && con2 && con3
        }
        if (randomFunction != null) {
            val annotation = target.getAnnotation(BaseObjects.randomizableFqName)
            if (annotation != null) {
                val builder = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = randomFunction.symbol,
                )
                val typeParamOfRandomFunction: List<IrTypeParameter> = randomFunction.typeParameters
                val randomConfigExpression = makeGetRandomConfigExpressionFromAnnotation(annotation, builder)
                val genericRandomFunctionParamList: List<IrValueParameter> = randomFunction.valueParameters.dropLast(1)

                val randomizersBuilderConfigFunctionParam =
                    requireNotNull(randomFunction.valueParameters.lastOrNull()) {
                        "randomizers function is missing. This is impossible, and is a bug by developer"
                    }

                val randomizerCollectionVar = makeRandomizerCollectionVar(builder, randomizersBuilderConfigFunctionParam, randomFunction)
                val getRandomizerCollection = builder.irGet(randomizerCollectionVar)

                val constructorCall = generateRandomClassInstance(
                    irClass = target,
                    getRandomConfigExpr = randomConfigExpression,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction,
                    genericRandomFunctionParamList = genericRandomFunctionParamList,
                    getRandomizerCollection = getRandomizerCollection
                )


                if (constructorCall != null) {
                    randomFunction.body = builder.irBlockBody {
                        +randomizerCollectionVar
                        +getRandomizerCollection
                        +builder.irReturn(constructorCall)
                    }
                    println(randomFunction.dumpKotlinLike())
                } else {
                    throw IllegalArgumentException("unable generate constructor call")
                }
            }
        }
    }

    private fun makeRandomizerCollectionVar(
        builder: DeclarationIrBuilder,
        randomizersBuilderConfigFunctionParam: IrValueParameter,
        randomFunction: IrFunction,
    ): IrVariable {

        val block = builder.irBlock(
            resultType = randomizerCollectionAccessor.irType,
            origin = BaseObjects.Ir.statementOrigin
        ) {

            val randomizersBuilderVar = irTemporary(
                randomizerCollectionBuilderImpAccessor.constructorFunction(this),
                nameHint = "randomizerCollectionBuilderTempVar"
            )

            val configRandomizerBuilderExpr = makeExprToConfigRandomizersBuilder(
                getRandomizersBuilderExpr = irGet(randomizersBuilderVar),
                randomizerBuilderConfigFunctionAsParam = randomizersBuilderConfigFunctionParam,
                builder = this
            )

            +configRandomizerBuilderExpr

            /**
             * Run the randomizer collection builder to get a randomizer collection.
             * Then store it in a var
             */
            val randomizerCollectionVar = irTemporary(
                buildRandomizersExpr(
                    builder = this,
                    getRandomizersBuilderExpr = irGet(randomizersBuilderVar),
                ),
                nameHint ="randomizerCollectionTempVar"
            )

           + irGet(randomizerCollectionVar)
        }

        val varRt = buildVariable(
            parent = randomFunction,
            startOffset = randomFunction.startOffset,
            endOffset = randomFunction.endOffset,
            origin = IrDeclarationOrigin.DEFINED,
            name = Name.identifier("randomizerCollection"),
            type = randomizerCollectionAccessor.irType
        ).withInit(block)

        return varRt
    }


    private fun makeRandomizerCollectionCodes(
        randomFunction: IrFunction,
        builder: DeclarationIrBuilder,
        randomizersBuilderConfigFunctionParam: IrValueParameter
    ): RandomizerCollectionCodes {

        val randomizersBuilderVar = makeRandomizerBuilderVar(randomFunction, builder)
        val getRandomizerBuilder = builder.irGet(randomizersBuilderVar)
        val configRandomizerBuilderExpr = makeExprToConfigRandomizersBuilder(
            getRandomizersBuilderExpr = getRandomizerBuilder,
            randomizerBuilderConfigFunctionAsParam = randomizersBuilderConfigFunctionParam,
            builder = builder
        )
        val randomizerCollectionVar = makeVarRandomizerCollection(
            buildRandomizerCollectionExpr = buildRandomizersExpr(
                builder = builder,
                getRandomizersBuilderExpr = getRandomizerBuilder
            ),
            randomFunction = randomFunction
        )

        val getRandomizerCollection = builder.irGet(randomizerCollectionVar)

        return RandomizerCollectionCodes(
            getRandomizerCollection,
            randomizersBuilderVar, configRandomizerBuilderExpr, randomizerCollectionVar
        )
    }


    private fun makeVarRandomizerCollection(
        buildRandomizerCollectionExpr: IrExpression,
        randomFunction: IrFunction,
    ): IrVariable {
        return buildVariable(
            parent = randomFunction,
            startOffset = randomFunction.startOffset,
            endOffset = randomFunction.endOffset,
            origin = IrDeclarationOrigin.DEFINED,
            name = Name.identifier("randomizerCollection"),
            type = randomizerCollectionAccessor.irType
        ).withInit(buildRandomizerCollectionExpr)

    }

    /**
     * Create an expression that build a [RandomizerCollection] from a [RandomizerCollectionBuilder] expression ([getRandomizersBuilderExpr])
     */
    private fun buildRandomizersExpr(
        builder: IrBuilderWithScope,
        getRandomizersBuilderExpr: IrExpression,
    ): IrExpression {
        return getRandomizersBuilderExpr.dotCall(randomizerCollectionBuilderAccessor.buildFunction(builder))
    }

    /**
     * Create an IrExpr to run a config function from [randomizerBuilderConfigFunctionAsParam]  on [RandomizerCollectionBuilder] from [getRandomizersBuilderExpr]
     */
    private fun makeExprToConfigRandomizersBuilder(
        getRandomizersBuilderExpr: IrExpression,
        randomizerBuilderConfigFunctionAsParam: IrValueParameter,
        builder: IrBuilderWithScope,
    ): IrExpression {

        return builder.irGet(randomizerBuilderConfigFunctionAsParam)
            .dotCall(function1Accessor.invokeFunction(builder))
            .withValueArgs(getRandomizersBuilderExpr)
    }

    /**
     * Call the constructor of
     */
    private fun makeRandomizerBuilderVar(
        randomFunction: IrFunction,
        builder: DeclarationIrBuilder,
    ): IrVariable {
        val randomizersBuilderVar = buildVariable(
            parent = randomFunction,
            startOffset = randomFunction.startOffset,
            endOffset = randomFunction.endOffset,
            origin = IrDeclarationOrigin.DEFINED,
            name = Name.identifier("randomizersBuilder"),
            type = randomizerCollectionBuilderAccessor.irType
        ).withInit(randomizerCollectionBuilderImpAccessor.constructorFunction(builder))

        return randomizersBuilderVar
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
            val getRandomConfigExpr = builder.irGet(randomConfigParam)
            val typeParamOfRandomFunction: List<IrTypeParameter> = randomFunction.typeParameters
            val genericRandomFunctionParamList: List<IrValueParameter> =
                randomFunction.valueParameters
                    //drop the first argument: it is a RandomConfig, not a generic factory lambda
                    .drop(1)
                    //drop the last argument: it is randomizer builder config lambda, not a generic factory lambda
                    .dropLast(1)

            val randomizersBuilderConfigFunctionParam = requireNotNull(randomFunction.valueParameters.lastOrNull()) {
                "randomizers function is missing. This is impossible, and is a bug by developer"
            }

            val randomizerCollectionCodes =
                makeRandomizerCollectionCodes(randomFunction, builder, randomizersBuilderConfigFunctionParam)

            val constructorCall = generateRandomClassInstance(
                irClass = target,
                getRandomConfigExpr = getRandomConfigExpr,
                builder = builder,
                typeParamOfRandomFunction = typeParamOfRandomFunction,
                genericRandomFunctionParamList = genericRandomFunctionParamList,
                getRandomizerCollection = randomizerCollectionCodes.getRandomCollectionExpr,
            )
            if (constructorCall != null) {
                randomFunction.body = builder.irBlockBody {
                    randomizerCollectionCodes.addCodeToBody(this)
                    +builder.irReturn(constructorCall)
                }
            } else {
                throw IllegalArgumentException("unable to generate constructor call")
            }
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
     * Construct an IrCall to access [DefaultRandomConfig.Companion.default]
     */
    private fun getDefaultRandomConfigInstance(builder: DeclarationIrBuilder): IrCall {
        return builder.irGetObject(defaultRandomConfigAccessor.defaultRandomConfigCompanionObject.symbol)
            .dotCall(builder.irCall(defaultRandomConfigAccessor.getDefaultRandomConfigInstance))
    }


    /**
     * Generate an [IrExpression] that can return a random instance of [irClass]
     */
    private fun generateRandomClassInstance(
        irClass: IrClass,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
        getRandomizerCollection: IrExpression,
    ): IrExpression? {
        val rt = stopAtFirstNotNullResult(
            { generateRandomObj(irClass, builder) },
            { generateRandomEnum(irClass, getRandomConfigExpr, builder) },
            {
                generateRandomConcreteClass(
                    irClass = irClass,
                    getRandomConfigExpr = getRandomConfigExpr,
                    builder = builder,
                    typeParamOfRandomFunction = typeParamOfRandomFunction,
                    genericRandomFunctionParamList = genericRandomFunctionParamList,
                    getRandomizerCollection = getRandomizerCollection
                )
            },
            {
                generateRandomSealClass(
                    irClass,
                    getRandomConfigExpr,
                    builder,
                    typeParamOfRandomFunction,
                    genericRandomFunctionParamList,
                    getRandomizerCollection = getRandomizerCollection
                )
            },
            {
                generateRandomAbstractClass(
                    irClass,
                    getRandomConfigExpr,
                    builder,
                    typeParamOfRandomFunction,
                    genericRandomFunctionParamList,
                    getRandomizerCollection = getRandomizerCollection
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
        irClass: IrClass,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
        getRandomizerCollection: IrExpression,
    ): IrExpression? {
        if (irClass.isFinalOrOpenConcrete() && !irClass.isObject && !irClass.isEnumClass) {
            val constructor = getConstructor(irClass)

            val paramExpressions = constructor.valueParameters.map { param ->
                generateRandomParam(
                    param = param,
                    builder = builder,
                    getRandomConfig = getRandomConfigExpr,
                    typeParamOfRandomFunction = typeParamOfRandomFunction,
                    genericRandomFunctionParamList = genericRandomFunctionParamList,
                    getRandomizerCollection = getRandomizerCollection,
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
     *    [truePart]
     * }else{
     *    null << always return null on else
     * }
     * ```
     */
    private fun randomIfElseNull(
        builder: DeclarationIrBuilder,
        /**
         * An expr to get a [RandomConfig]
         */
        getRandomConfigExpr: IrExpression,
        /**
         * this is the return type of the if-else expr
         */
        type: IrType,
        truePart: IrExpression,
    ): IrExpression {
        return randomIfElse(builder, getRandomConfigExpr, type, truePart, builder.irNull())
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
        getRandomConfigExpr: IrExpression,
        /**
         * this is the return type of the if-else expr
         */
        type: IrType,
        truePart: IrExpression,
        elsePart: IrExpression
    ): IrExpression {
        val conditionExpr = getRandomConfigExpr.dotCall(randomConfigAccessor.nextBoolean(builder))
        return builder.irIfThenElse(
            type = type,
            condition = conditionExpr,
            thenPart = truePart,
            elsePart = elsePart
        )
    }

    private fun generateRandomSealClass(
        irClass: IrClass,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
        getRandomizerCollection: IrExpression,
    ): IrExpression? {
        if (irClass.isSealed()) {
            TODO()
        } else {
            return null
        }
    }

    private fun generateRandomAbstractClass(
        irClass: IrClass,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
        getRandomizerCollection: IrExpression,
    ): IrExpression? {
        if (irClass.isAbstract() && !irClass.isSealed() && irClass.isAnnotatedWith(BaseObjects.randomizableFqName)) {
            TODO("not supported yet")
        } else {
            return null
        }
    }


    private fun generateRandomParam(
        param: IrValueParameter,
        builder: DeclarationIrBuilder,
        /**
         * An expression that return a [RandomConfig]
         */
        getRandomConfig: IrExpression,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        genericRandomFunctionParamList: List<IrValueParameter>,
        /**
         * get  random collection, this should return a reused variable, instead create anything new
         */
        getRandomizerCollection: IrExpression,
    ): IrExpression {
        val primitive = generatePrimitiveRandomParam(param, builder, getRandomConfig)
        if (primitive != null) {
            return primitive
        } else {
            val paramType = param.type
            if (paramType.isTypeParameter()) {

                val nonNullRandom = builder.irBlock {
                    /**
                     * random from generic function
                     */
                    val randomFromGenericFactoryFunction = run {
                        val paramTypeIndex = (paramType.classifierOrFail as IrTypeParameterSymbol).owner.index
                        val irGetLambda = builder.irGet(genericRandomFunctionParamList[paramTypeIndex])
                        irGetLambda
                            .dotCall(function1Accessor.invokeFunction(builder))
                            .withValueArgs(getRandomConfig)
                    }

                    /**
                     * Generate random for generic param under this order of priority:
                     * random collection > generic factory function
                     */

                    val randomFromCollectionCall = getRandomizerCollection
                        .extensionDotCall(randomizerCollectionAccessor.randomFunction(builder))
                        .withTypeArgs(paramType)

                    val randomFromCollectionVar = irTemporary(randomFromCollectionCall,"randomFromCollectionVar")


                    val getRandomFromCollection = irGet(randomFromCollectionVar)

                    + builder.irIfNull(
                        type = paramType,
                        subject = getRandomFromCollection,
                        thenPart = randomFromGenericFactoryFunction,
                        elsePart = getRandomFromCollection
                    )
                }

                val rt = if (paramType.isNullable()) {
                    randomIfElseNull(
                        builder = builder,
                        getRandomConfigExpr = getRandomConfig,
                        type = paramType,
                        truePart = nonNullRandom,
                    )
                } else {
                    nonNullRandom
                }

                return rt
            } else {

                val paramClass = paramType.classOrNull?.owner

                if (paramClass != null) {


                    /**
                     * random instance from randomizer level 0
                     */
                    val randomFromLevel0 = generateRandomClassInstance(
                        paramClass,
                        getRandomConfig,
                        builder,
                        typeParamOfRandomFunction,
                        genericRandomFunctionParamList,
                        getRandomizerCollection
                    )

                    if (randomFromLevel0 != null) {

                        val nonNullRandom = builder.irBlock {
                            /**
                             * random instance from random collection
                             */
                            val randomFromCollectionCall = getRandomizerCollection
                                .extensionDotCall(randomizerCollectionAccessor.randomFunction(builder))
                                .withTypeArgs(paramType)

                            val randomFromCollectionVar = irTemporary(randomFromCollectionCall,"randomFromCollectionVar")

                            val getRandomFromCollection = irGet(randomFromCollectionVar)

                            + builder.irIfNull(
                                type = paramType,
                                subject = getRandomFromCollection,
                                thenPart = randomFromLevel0,
                                elsePart = getRandomFromCollection
                            )
                        }

                        if (paramType.isNullable()) {
                            return randomIfElse(
                                builder, getRandomConfig, paramType,
                                truePart = nonNullRandom,
                                elsePart = builder.irNull(),
                            )
                        } else {
                            return nonNullRandom
                        }
                    } else {
                        throw IllegalArgumentException("unable to construct an expression to generate a random instance for $param")
                    }
                } else {
                    throw IllegalArgumentException("$param does not belong to a class :| ")
                }
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
        getRandomConfig: IrExpression
    ): IrExpression? {

        val paramType = param.type

        if (paramType.isNullable()) {
            return when {
                paramType.isInt2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextIntOrNull(builder))
                paramType.isUInt2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextUIntOrNull(builder))
                paramType.isLong2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextLongOrNull(builder))
                paramType.isULong2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextULongOrNull(builder))
                paramType.isByte2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextByteOrNull(builder))
                paramType.isUByte2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextUByteOrNull(builder))
                paramType.isShort2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextShortOrNull(builder))
                paramType.isUShort2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextUShortOrNull(builder))
                paramType.isBoolean2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextBoolOrNull(builder))
                paramType.isFloat2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextFloatOrNull(builder))
                paramType.isDouble2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextDoubleOrNull(builder))
                paramType.isChar2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextCharOrNull(builder))
                paramType.isString2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextStringUUIDOrNull(builder))
                paramType.isUnit2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextUnitOrNull(builder))
                paramType.isNumber2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextNumberOrNull(builder))
                paramType.isAny2(true) -> getRandomConfig.dotCall(randomConfigAccessor.nextAnyOrNull(builder))
                paramType.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")

                else -> null
            }
        } else {
            val rt = when {
                paramType.isInt2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextInt(builder))
                paramType.isUInt2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextUInt(builder))
                paramType.isLong2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextLong(builder))
                paramType.isULong2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextULong(builder))
                paramType.isByte2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextByte(builder))
                paramType.isUByte2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextUByte(builder))
                paramType.isShort2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextShort(builder))
                paramType.isUShort2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextUShort(builder))
                paramType.isBoolean2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextBoolean(builder))
                paramType.isFloat2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextFloat(builder))
                paramType.isDouble2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextDouble(builder))
                paramType.isChar2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextChar(builder))
                paramType.isString2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextStringUUID(builder))
                paramType.isUnit2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextUnit(builder))
                paramType.isNumber2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextNumber(builder))
                paramType.isAny2(false) -> getRandomConfig.dotCall(randomConfigAccessor.nextAny(builder))
                paramType.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")

                else -> null
            }

            return rt


        }
    }
}
