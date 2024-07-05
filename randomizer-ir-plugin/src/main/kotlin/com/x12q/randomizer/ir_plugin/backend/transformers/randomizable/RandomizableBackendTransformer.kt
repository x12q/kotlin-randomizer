package com.x12q.randomizer.ir_plugin.backend.transformers.randomizable

import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.RandomAccessor
import com.x12q.randomizer.ir_plugin.backend.transformers.accesor.RandomConfigAccessor
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.backend.transformers.utils.dotCall
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

@OptIn(UnsafeDuringIrConstructionAPI::class)
class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
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

    private val randomAccessor by lazy { RandomAccessor(kotlinRandomClass,pluginContext) }

    private val randomConfigClass by lazy {
        val clzz = pluginContext.referenceClass(BaseObjects.randomConfigClassId)
        requireNotNull(clzz) {
            "RandomConfig interface is not in the class path."
        }
        clzz
    }

    private val randomConfigAccessor by lazy {
        RandomConfigAccessor(randomConfigClass)
    }

    private val defaultRandomConfigClass by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.defaultRandomConfigClassId)) {
            "impossible, DefaultRandomConfig class must exist in the class path"
        }
    }

    private val defaultRandomConfigCompanion by lazy {
        requireNotNull(defaultRandomConfigClass.owner.companionObject()){
            "impossible, ${BaseObjects.defaultConfigClassName}.Companion must exist"
        }
    }

    private val getDefaultRandomConfigInstance by lazy {
        if(defaultRandomConfigCompanion.isObject){
            requireNotNull(defaultRandomConfigCompanion.getPropertyGetter("default")){
                "Impossible, ${BaseObjects.defaultConfigClassName}.Companion must contain a \"default\" variable"
            }
        }else{
            throw IllegalArgumentException("Impossible, ${BaseObjects.defaultConfigClassName}.CompanionObject must be an object")
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
     * Create an IR expression that returns a [RandomConfig] instance from @Randomizable annotation
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

            val providedArgumentClassSymbol = (providedArgument as? IrClassReference)?.classType?.classOrNull

            if (providedArgumentClassSymbol != null) {
                val providedArgumentIrClass = providedArgumentClassSymbol.owner
                if (providedArgumentIrClass.isObject) {
                    return builder.irGetObject(providedArgumentClassSymbol)
                } else if (providedArgumentIrClass.isClass) {
                    val constructor = providedArgumentIrClass.primaryConstructor
                    if (constructor != null) {
                        if (constructor.valueParameters.isEmpty()) {
                            return builder.irCall(constructor)
                        } else {
                            throw IllegalArgumentException("${providedArgumentIrClass.name}: RandomConfig class primary constructor must have zero parameter")
                        }
                    } else {
                        throw IllegalArgumentException("${providedArgumentIrClass.name}: RandomConfig class must have a primary constructor")
                    }
                } else {
                    throw IllegalArgumentException("${providedArgumentIrClass.name} must either be a class or an object")
                }
            } else {
                throw IllegalArgumentException("$providedArgument must be a KClass")
            }
        } else {

            val randomConfigParam: IrValueParameter? = randomConfigArgumentParamData?.first
            if (randomConfigParam?.hasDefaultValue() == true) {
                /**
                 * This is DefaultConfig.Companion.default
                 */
                return builder.irGetObject(defaultRandomConfigCompanion.symbol).dotCall(builder.irCall(getDefaultRandomConfigInstance))

            } else {
                throw IllegalArgumentException("impossible, a default class or object must be provided for @Randomizable, this is a mistake by the developer")
            }
        }
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
        val primType = paramType.getPrimitiveType()

        if (primType != null) {
            val rt = when (primType) {
                PrimitiveType.BOOLEAN -> getRandom.dotCall(randomAccessor.nextBoolean(builder))
                PrimitiveType.CHAR -> getRandomConfig.dotCall (randomConfigAccessor.nextChar(builder))
                PrimitiveType.BYTE -> getRandomConfig.dotCall(randomConfigAccessor.nextByte(builder))
                PrimitiveType.SHORT -> 123.toShort().toIrConst(pluginContext.irBuiltIns.shortType)
                PrimitiveType.INT -> getRandom.dotCall(randomAccessor.nextInt(builder))
                PrimitiveType.FLOAT -> getRandom.dotCall(randomAccessor.nextFloat(builder))
                PrimitiveType.LONG -> getRandom.dotCall(randomAccessor.nextLong(builder))
                PrimitiveType.DOUBLE -> getRandom.dotCall(randomAccessor.nextDouble(builder))
                else -> {
                    throw IllegalArgumentException("not support primitive type $primType")
                }
            }
            return rt
        } else {
            return null
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
