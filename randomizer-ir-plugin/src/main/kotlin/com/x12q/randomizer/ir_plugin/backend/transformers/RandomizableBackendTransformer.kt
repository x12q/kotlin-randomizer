package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.BasicClassAccessor
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.RandomAccessor
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.RandomConfigAccessor
import com.x12q.randomizer.ir_plugin.backend.transformers.utils.*
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import javax.inject.Inject

@OptIn(UnsafeDuringIrConstructionAPI::class)
class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
    private val randomAccessor: RandomAccessor,
    private val randomConfigAccessor: RandomConfigAccessor,
    private val basicClassAccessor: BasicClassAccessor
) : RDBackendTransformer() {

    override fun visitClassNew(declaration: IrClass): IrStatement {

        val annotation = declaration.getAnnotation(BaseObjects.randomizableFqName)
        if (annotation != null) {
            val companionObj = declaration.companionObject()
            if (companionObj != null) {
                completeRandomFunction(companionObj, declaration)
                completeRandomFunctionWithRandomConfig(companionObj, declaration)
            }
        }
        return super.visitClassNew(declaration)
    }

    /**
     * Create an IR expression that returns a [RandomConfig] instance from [Randomizable] annotation
     */
    private fun makeRandomConfigExpressionFromAnnotation(
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
        return builder.irGetObject(basicClassAccessor.defaultRandomConfigCompanionObject.symbol)
            .dotCall(builder.irCall(basicClassAccessor.getDefaultRandomConfigInstance))
    }

    /**
     * complete random() function in [companionObj].
     * This function use the random config in annotation.
     */
    private fun completeRandomFunction(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            function.name == BaseObjects.randomFunctionName && function.valueParameters.isEmpty()
        }
        if (randomFunction != null) {
            val annotation = target.getAnnotation(BaseObjects.randomizableFqName)
            if (annotation != null) {
                val builder = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = randomFunction.symbol,
                )
                val randomConfigExpression = makeRandomConfigExpressionFromAnnotation(annotation, builder)

                val constructor = target.primaryConstructor

                if (constructor != null) {
                    val paramExpressions = constructor.valueParameters.map { param ->
                        generatePrimitiveRandomParam(param, builder, randomConfigExpression)
                    }


                    val constructorCall = builder.irCallConstructor(
                        callee = constructor.symbol,
                        /**
                         * TODO need to be dealt with this when work with generic
                         */
                        typeArguments = emptyList()
                    ).apply {
                        paramExpressions.withIndex().forEach { (index, paramExp) ->
                            putValueArgument(index, paramExp)
                        }

                    }

                    randomFunction.body = builder.irBlockBody {
                        +builder.irReturn(
                            constructorCall
                        )
                    }
                } else {
                    throw IllegalArgumentException("$target does not have a constructor")
                }
            }
        }
    }

    /**
     * Complete random(randomConfig) function
     */
    private fun completeRandomFunctionWithRandomConfig(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            function.name == BaseObjects.randomFunctionName && function.valueParameters.size == 1
        }

        if (randomFunction != null) {
            val builder = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = randomFunction.symbol,
            )

            val randomConfigParam = randomFunction.valueParameters.firstOrNull {
                it.name == BaseObjects.randomConfigParamName
            }!!

            val getRandomConfigExpr = builder.irGet(randomConfigParam)

            val constructor = target.primaryConstructor
            if (constructor != null) {

                val paramExpressions = constructor.valueParameters.map { param ->
                    generatePrimitiveRandomParam(param, builder, getRandomConfigExpr)
                }

                val constructorCall =
                    builder.irCallConstructor(
                        callee = constructor.symbol,
                        /**
                         * TODO need to be dealt with this when work with generic
                         */
                        typeArguments = emptyList()
                    ).apply {
                        paramExpressions.withIndex().forEach { (index, paramExp) ->
                            putValueArgument(index, paramExp)
                        }
                    }

                randomFunction.body = builder.irBlockBody {
                    +builder.irReturn(
                        constructorCall
                    )
                }
            } else {
                throw IllegalArgumentException("$target does not have a constructor")
            }
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

        val getRandom = getRandomConfig.dotCall(randomConfigAccessor.random(builder))
        val paramType = param.type
        val builtInTypes = pluginContext.irBuiltIns

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
                    getRandomConfig.dotCall(randomConfigAccessor.nextUnitOrNull(builder))
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
                    getRandom.dotCall(randomAccessor.nextBoolean(builder)),
                    getRandom.dotCall(randomAccessor.nextInt(builder)),
                    getRandom.dotCall(randomAccessor.nextFloat(builder)),
                    getRandom.dotCall(randomAccessor.nextLong(builder)),
                    getRandom.dotCall(randomAccessor.nextDouble(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextChar(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextByte(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextStringUUID(builder)),
                    getRandomConfig.dotCall(randomConfigAccessor.nextUnit(builder))
                ).random()

                paramType.isNothing2() -> throw IllegalArgumentException("impossible to randomize ${Nothing::class.qualifiedName}")

                else -> null
            }

            return rt


        }
    }
}
