package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.RandomAccessor
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.RandomConfigAccessor
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.backend.transformers.utils.dotCall
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

@OptIn(UnsafeDuringIrConstructionAPI::class)
class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
    private val randomAccessFactory: RandomAccessor.Factory,
    private val randomConfigAccessorFactory: RandomConfigAccessor.Factory,
) : RDBackendTransformer() {

    val globalConfigName = "com.x12q.randomizer.GlobalRandomConfig"
    val configAnnotation = "com.x12q.randomizer.annotations.RandomizerConfig"

    private val dumpBuilder: StringBuilder = StringBuilder()

    private val kotlinRandomClass by lazy {
        val clzz = pluginContext.referenceClass(BaseObjects.randomClassId)
        requireNotNull(clzz) {
            "kotlin.random.Random class is not in the class path."
        }
        clzz
    }

    private val randomAccessor by lazy { randomAccessFactory.create(kotlinRandomClass) }

    private val randomConfigClass by lazy {
        val clzz = pluginContext.referenceClass(BaseObjects.randomConfigClassId)
        requireNotNull(clzz) {
            "RandomConfig interface is not in the class path."
        }
        clzz
    }

    private val randomConfigAccessor by lazy {
        randomConfigAccessorFactory.create(randomConfigClass)
    }

    private val defaultRandomConfigClass by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.defaultRandomConfigClassId)) {
            "impossible, DefaultRandomConfig class must exist in the class path"
        }
    }

    private val defaultRandomConfigCompanionObject by lazy {
        requireNotNull(defaultRandomConfigClass.owner.companionObject()) {
            "impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must exist"
        }
    }

    private val getDefaultRandomConfigInstance by lazy {
        if (defaultRandomConfigCompanionObject.isObject) {
            requireNotNull(defaultRandomConfigCompanionObject.getPropertyGetter("default")) {
                "Impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must contain a \"default\" variable"
            }
        } else {
            throw IllegalArgumentException("Impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must be an object")
        }
    }

    val irFactory = pluginContext.irFactory

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
                            throw IllegalArgumentException("${providedArgumentIrClass.name} must NOT be abstract")
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
        return builder.irGetObject(defaultRandomConfigCompanionObject.symbol)
            .dotCall(builder.irCall(getDefaultRandomConfigInstance))
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

                    val constructorCall =
                        builder.irCallConstructor(
                            constructor.symbol,
                            constructor.valueParameters.map { it.type }).apply {
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

            val constructor = target.primaryConstructor
            if (constructor != null) {

                val paramExpressions = constructor.valueParameters.map { param ->
                    val getRandomConfigExpr = builder.irGet(randomConfigParam)
                    generatePrimitiveRandomParam(param, builder, getRandomConfigExpr)
                }

                val constructorCall =
                    builder.irCallConstructor(constructor.symbol, constructor.valueParameters.map { it.type }).apply {
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
        /**
         * parameter IR
         */
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

        return when (paramType) {
            builtInTypes.booleanType -> getRandom.dotCall(randomAccessor.nextBoolean(builder))
            builtInTypes.intType -> getRandom.dotCall(randomAccessor.nextInt(builder))
            builtInTypes.floatType -> getRandom.dotCall(randomAccessor.nextFloat(builder))
            builtInTypes.longType -> getRandom.dotCall(randomAccessor.nextLong(builder))
            builtInTypes.doubleType -> getRandom.dotCall(randomAccessor.nextDouble(builder))

            builtInTypes.charType -> getRandomConfig.dotCall(randomConfigAccessor.nextChar(builder))
            builtInTypes.byteType -> getRandomConfig.dotCall(randomConfigAccessor.nextByte(builder))
            builtInTypes.shortType -> getRandomConfig.dotCall { randomConfigAccessor.nextShort(builder) }
            builtInTypes.stringType -> getRandomConfig.dotCall { randomConfigAccessor.nextStringUUID(builder) }
            builtInTypes.unitType -> getRandomConfig.dotCall { randomConfigAccessor.nextUnit(builder) }
            builtInTypes.numberType -> getRandomConfig.dotCall { randomConfigAccessor.nextNumber(builder) }
            else -> null
        }
    }

    fun IrElement.dumpToDump() {
        dumpBuilder.appendLine(
            this.dump()
        )
    }


    /**
     * Alter the body of [declaration], so that a println statement that prints out the dump of the function itself is added at the beginning of the function body.
     */
    override fun visitFunctionNew(declaration: IrFunction): IrStatement {

        val oldBody = declaration.body

        if (oldBody != null && isSomeFunction(declaration)) {
            val declarationBuilder = DeclarationIrBuilder(
                generatorContext = pluginContext, symbol = declaration.symbol
            )

            val newBody = declarationBuilder.irBlockBody {
                // create and add the print dump call in the new function body
                +printDumpCall(
                    pluginContext, declaration, this
                )
                // preserve the statements in the old function body
                for (statement in oldBody.statements) {
                    +statement
                }
            }

            declaration.body = newBody
        }

        return super.visitFunctionNew(declaration)
    }

    private fun isSomeFunction(irFun: IrFunction): Boolean {
        val funName = irFun.name
        return funName == Name.identifier("someFunction")
    }

    private fun printDumpCall(
        pluginContext: IrPluginContext,
        targetFunction: IrFunction,
        irBuilder: IrBuilderWithScope,
    ): IrCall {
        val printlnCallId = BaseObjects.Std.printlnCallId
        val strIR = irBuilder.irString(
            dumpBuilder.toString()
        )
        val anyNullableType = pluginContext.irBuiltIns.anyNType
        val funPrintln = pluginContext.referenceFunctions(printlnCallId).first { funSymbol ->
            val parameters = funSymbol.owner.valueParameters
            parameters.size == 1 && parameters[0].type == anyNullableType
        }

        val call = irBuilder.irCall(funPrintln)

        call.putValueArgument(0, strIR)

        return call

    }
}
