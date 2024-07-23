package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.*
import com.x12q.randomizer.ir_plugin.backend.utils.*
import com.x12q.randomizer.ir_plugin.backend.utils.isDouble2
import com.x12q.randomizer.ir_plugin.backend.utils.isFloat2
import com.x12q.randomizer.ir_plugin.backend.utils.isLong2
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.stopAtFirstNotNullResult
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import javax.inject.Inject

@OptIn(UnsafeDuringIrConstructionAPI::class)
class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
    private val randomAccessor: RandomAccessor,
    private val randomConfigAccessor: RandomConfigAccessor,
    private val basicAccessor: BasicAccessor,
    private val function0Accessor: Function0Accessor,
    private val function1Accessor: Function1Accessor,
) : RDBackendTransformer() {

    val origin = BaseObjects.declarationOrigin

    override fun visitClassNew(declaration: IrClass): IrStatement {

        val annotation = declaration.getAnnotation(BaseObjects.randomizableFqName)
        if (annotation != null) {
            val companionObj = declaration.companionObject()
            if (companionObj != null) {
                completeRandomFunction(companionObj, declaration)
                completeRandomFunctionWithRandomConfig(companionObj, declaration)
                completeRandomizerFunction(companionObj, declaration)
            }
        }
        return super.visitClassNew(declaration)
    }

    /**
     * A Randomizer function is one that return an instance of randomizer declared inside the annotation
     * TODO complete this
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
                    target, randomConfigExpression, builder,
                    emptyList(), emptyList()
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
     */
    private fun completeRandomFunction(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            function.name == BaseObjects.randomFunctionName && function.valueParameters.size == target.typeParameters.size
        }
        if (randomFunction != null) {
            val annotation = target.getAnnotation(BaseObjects.randomizableFqName)
            if (annotation != null) {
                val builder = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = randomFunction.symbol,
                )
                val typeParamOfRandomFunction: List<IrTypeParameter> = randomFunction.typeParameters
                val valueParamsOfRandomFunction: List<IrValueParameter> = randomFunction.valueParameters

                val randomConfigExpression = makeGetRandomConfigExpressionFromAnnotation(annotation, builder)
                val constructorCall = generateRandomClassInstance(
                    target,
                    randomConfigExpression,
                    builder,
                    typeParamOfRandomFunction,
                    valueParamsOfRandomFunction
                )
                if (constructorCall != null) {
                    randomFunction.body = builder.irBlockBody {
                        +builder.irReturn(
                            constructorCall
                        )
                    }
                } else {
                    throw IllegalArgumentException("unable generate constructor call")
                }
            }
        }
    }

    /**
     * Complete random(randomConfig) function
     */
    private fun completeRandomFunctionWithRandomConfig(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            function.name == BaseObjects.randomFunctionName && function.valueParameters.size == target.typeParameters.size + 1
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
            val valueParamsOfRandomFunction: List<IrValueParameter> = randomFunction.valueParameters.drop(1) //drop 1 because randomConfig is not relevant
            val constructorCall = generateRandomClassInstance(
                target,
                getRandomConfigExpr,
                builder,
                typeParamOfRandomFunction,
                valueParamsOfRandomFunction
            )
            if (constructorCall != null) {
                randomFunction.body = builder.irBlockBody {
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
                providedArgumentClassSymbol.owner.classId == BaseObjects.defaultRandomConfigClassId

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
        return builder.irGetObject(basicAccessor.defaultRandomConfigCompanionObject.symbol)
            .dotCall(builder.irCall(basicAccessor.getDefaultRandomConfigInstance))
    }


    /**
     * Generate an [IrExpression] that can return a random instance of [irClass]
     */
    private fun generateRandomClassInstance(
        irClass: IrClass,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        valueParamsOfRandomFunction: List<IrValueParameter>,
    ): IrExpression? {
        val rt = stopAtFirstNotNullResult(
            { generateRandomObj(irClass, builder) },
            { generateRandomEnum(irClass, getRandomConfigExpr, builder) },
            {
                generateRandomConcreteClass(
                    irClass,
                    getRandomConfigExpr,
                    builder,
                    typeParamOfRandomFunction,
                    valueParamsOfRandomFunction
                )
            },
            {
                generateRandomSealClass(
                    irClass,
                    getRandomConfigExpr,
                    builder,
                    typeParamOfRandomFunction,
                    valueParamsOfRandomFunction
                )
            },
            {
                generateRandomAbstractClass(
                    irClass,
                    getRandomConfigExpr,
                    builder,
                    typeParamOfRandomFunction,
                    valueParamsOfRandomFunction
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
                    .extensionDotCall(builder.irCall(basicAccessor.randomFunctionOnCollectionOneArg))
                    .args(getRandom)

                return rt
            } else {
                val irValues =
                    irClass.declarations.firstOrNull { it.getNameWithAssert().toString() == "values" } as? IrFunction

                if (irValues != null) {
                    val randomFunction = basicAccessor.randomFunctionOnArrayOneArg
                    val rt = builder.irCall(irValues)
                        .extensionDotCall(builder.irCall(randomFunction))
                        .args(getRandom)
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
        valueParamsOfRandomFunction: List<IrValueParameter>,
    ): IrExpression? {
        if (irClass.isFinalOrOpenConcrete() && !irClass.isObject && !irClass.isEnumClass) {
            val constructor = getConstructor(irClass)

            val paramExpressions = constructor.valueParameters.map { param ->
                generateRandomParam(
                    param,
                    builder,
                    getRandomConfigExpr,
                    typeParamOfRandomFunction,
                    valueParamsOfRandomFunction
                )
            }

            val x = typeParamOfRandomFunction.map { it.defaultType }
            val constructorCall = builder.irCallConstructor(
                callee = constructor.symbol,
                typeArguments = emptyList()
            ).apply {
                paramExpressions.forEachIndexed { index, paramExp ->
                    putValueArgument(index, paramExp)
                }
            }
            return constructorCall
        } else {
            return null
        }
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
    private fun randomIfElse(builder: DeclarationIrBuilder, getRandomConfigExpr: IrExpression, type: IrType,truePart:IrExpression, elseExpr:IrExpression):IrExpression{
        val conditionExpr = getRandomConfigExpr.dotCall(randomConfigAccessor.nextBoolean(builder))
        return builder.irIfThenElse(
            type = type,
            condition = conditionExpr,
            thenPart = truePart,
            elsePart = elseExpr
        )
    }

    private fun generateRandomSealClass(
        irClass: IrClass,
        getRandomConfigExpr: IrExpression,
        builder: DeclarationIrBuilder,
        typeParamOfRandomFunction: List<IrTypeParameter>,
        valueParamsOfRandomFunction: List<IrValueParameter>,
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
        valueParamsOfRandomFunction: List<IrValueParameter>,
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
        valueParamsOfRandomFunction: List<IrValueParameter>,
    ): IrExpression? {
        val primitive = generatePrimitiveRandomParam(param, builder, getRandomConfig)
        if (primitive != null) {
            return primitive
        } else {
            val paramType = param.type
            if (paramType.isTypeParameter()) {
                // call the factory function to generate random generic
                val paramTypeIndex = (paramType.classifierOrFail as IrTypeParameterSymbol).owner.index
                val irGetLambda = builder.irGet(valueParamsOfRandomFunction[paramTypeIndex])

                return irGetLambda.dotCall(function1Accessor.invokeFunction(builder)).apply {
                    this.putValueArgument(0,getRandomConfig)
                }

            } else {
                if (paramType.isNullable()) {
                    TODO("handle nullable param")
                } else {
                    val nestedClass = paramType.classOrNull?.owner
                    if (nestedClass != null) {
                        val rt = generateRandomClassInstance(
                            nestedClass,
                            getRandomConfig,
                            builder,
                            typeParamOfRandomFunction,
                            valueParamsOfRandomFunction
                        )
                        return rt
                    } else {
                        TODO("may need to handle generic here")
                    }
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
                paramType.isAny2(true) -> listOf(
                    getRandomConfig.dotCall(randomConfigAccessor.nextIntOrNull(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextBoolOrNull(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextFloatOrNull(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextLongOrNull(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextDoubleOrNull(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextCharOrNull(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextByteOrNull(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextStringUUIDOrNull(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextUnitOrNull(builder)),
                ).random()

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
                paramType.isAny2(false) -> listOf(
                    getRandomConfig.dotCall(randomConfigAccessor.nextBoolean(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextInt(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextFloat(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextLong(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextDouble(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextChar(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextByte(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextStringUUID(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextUnit(builder)),
                ).random()

                paramType.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")

                else -> null
            }

            return rt


        }
    }
}
