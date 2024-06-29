package com.x12q.randomizer.ir_plugin.backend.transformers.randomizable

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.backend.transformers.utils.Standards
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class RandomizableBackendTransformer @Inject constructor(
    override val pluginContext: IrPluginContext,
) : RDBackendTransformer() {

    val globalConfigName = "com.x12q.randomizer.GlobalRandomConfig"
    val configAnnotation = "com.x12q.randomizer.annotations.RandomizerConfig"

    private val dumpBuilder: StringBuilder = StringBuilder()

    val irFactory = pluginContext.irFactory

    override fun visitClassNew(declaration: IrClass): IrStatement {

        val annotation = declaration.getAnnotation(BaseObjects.randomizableFqName)
        if (annotation != null) {
            val irClass = declaration
            val companionObj = irClass.companionObject()
            if (companionObj != null) {
                completeRandomFunction2(companionObj, declaration)
                completeRandomFunction1(companionObj, declaration)

            }
        }
        return super.visitClassNew(declaration)
    }

    /**
     * complete random() function in [companionObj].
     * This function use the random config in annotation.
     */
    private fun completeRandomFunction1(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { function ->
            function.name == BaseObjects.randomFunctionName && function.valueParameters.isEmpty()
        }
        if (randomFunction != null) {
            val annotation = target.getAnnotation(BaseObjects.randomizableFqName)
            if (annotation != null) {
                val randomConfigClassParam: IrValueParameter? =
                    annotation.getAllArgumentsWithIr().firstOrNull { (irParam, irExpr) ->
                        irParam.name == BaseObjects.randomConfigParamName
                    }?.first

                if (randomConfigClassParam != null) {

                    val builder = DeclarationIrBuilder(
                        generatorContext = pluginContext,
                        symbol = randomFunction.symbol,
                    )
                    // this one is a KClass, it does not know about the type passed to it. I have to use something else
                    val randomConfigParamKClassSymbol = randomConfigClassParam.type.classOrNull
                    if (randomConfigParamKClassSymbol != null) {
                        if (randomConfigParamKClassSymbol.owner.isObject) {
                            val getRandomConfigObject =
                                builder.irCall(randomConfigParamKClassSymbol.getPropertyGetter("objectInstance")!!)
                                    .apply {
                                        dispatchReceiver = builder.irGet(randomConfigClassParam)
                                    }

                            val constructor = target.primaryConstructor
                            if (constructor != null) {

                                val paramExpressions = constructor.valueParameters.map { param ->
                                    randomPrimitiveParam3(param, builder, getRandomConfigObject)
//                                randomPrimitiveParam(param, builder)
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
                        } else {
//                            TODO()
                        }
                    }


                }
            }
        }
    }

    /**
     * Complete random(randomConfig) function
     */
    private fun completeRandomFunction2(companionObj: IrClass, target: IrClass) {
        val randomFunction = companionObj.findDeclaration<IrSimpleFunction> { f ->
            f.name == BaseObjects.randomFunctionName && f.valueParameters.size == 1
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
                    randomPrimitiveParam2(param, builder, randomConfigParam)
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
     * randomize a parameter while taking random config into consideration
     */
    private fun randomPrimitiveParam3(
        param: IrValueParameter,
        builder: DeclarationIrBuilder,
        getRandomConfig: IrCall
    ): IrExpression {

        val randomConfigClass = getRandomConfig.symbol.owner.returnType.classOrNull
        val nextIntFunctionSymbol = randomConfigClass!!.functions.firstOrNull { functionSym ->
            functionSym.owner.name == Name.identifier("nextInt") && functionSym.owner.valueParameters.isEmpty()
        }

        val nextIntCall = builder.irCall(nextIntFunctionSymbol!!).apply {
            dispatchReceiver = getRandomConfig
        }

        val paramType = param.type
        val primType = paramType.getPrimitiveType()

        val rt = when (primType) {
            PrimitiveType.BOOLEAN -> builder.irBoolean(true)
            PrimitiveType.CHAR -> builder.irChar('z')
            PrimitiveType.BYTE -> 1.toByte().toIrConst(pluginContext.irBuiltIns.byteType)
            PrimitiveType.SHORT -> 123.toShort().toIrConst(pluginContext.irBuiltIns.shortType)
            PrimitiveType.INT -> nextIntCall
            PrimitiveType.FLOAT -> 333f.toIrConst(pluginContext.irBuiltIns.floatType)
            PrimitiveType.LONG -> builder.irLong(123L)
            PrimitiveType.DOUBLE -> 999.0.toIrConst(pluginContext.irBuiltIns.doubleType)
            else -> {
                TODO()
            }
        }
        return rt

    }

    private fun randomPrimitiveParam2(
        param: IrValueParameter,
        builder: DeclarationIrBuilder,
        randomConfig: IrValueParameter
    ): IrExpression {

        val randomArg = builder.irGet(randomConfig)
        val randomConfigClassSymbol = randomArg.type.classOrNull

        if (randomConfigClassSymbol != null) {

            val getRandomConfig = builder.irGet(randomConfig)
            val getRandomObj = builder.irCall(randomConfigClassSymbol.getPropertyGetter("random")!!).apply {
                dispatchReceiver = getRandomConfig
            }

            val nextIntFunction =
                getRandomObj.symbol.owner.returnType.classOrNull!!.functions.firstOrNull { functionSym ->
                    functionSym.owner.name == Name.identifier("nextInt") && functionSym.owner.valueParameters.isEmpty()
                }!!

            val nextIntCall = builder.irCall(nextIntFunction).apply {
                dispatchReceiver = getRandomObj
            }


            val paramType = param.type
            val primType = paramType.getPrimitiveType()

            val rt = when (primType) {
                PrimitiveType.BOOLEAN -> builder.irBoolean(true)
                PrimitiveType.CHAR -> builder.irChar('z')
                PrimitiveType.BYTE -> 1.toByte().toIrConst(pluginContext.irBuiltIns.byteType)
                PrimitiveType.SHORT -> 123.toShort().toIrConst(pluginContext.irBuiltIns.shortType)
                PrimitiveType.INT -> nextIntCall
                PrimitiveType.FLOAT -> 333f.toIrConst(pluginContext.irBuiltIns.floatType)
                PrimitiveType.LONG -> builder.irLong(123L)
                PrimitiveType.DOUBLE -> 999.0.toIrConst(pluginContext.irBuiltIns.doubleType)
                else -> {
                    TODO()
                }
            }
            return rt
        }
        TODO()
    }


    private fun randomPrimitiveParam(param: IrValueParameter, builder: DeclarationIrBuilder): IrExpression {

        val type = param.type
        val primType = type.getPrimitiveType()

        val rt = when (primType) {
            PrimitiveType.BOOLEAN -> builder.irBoolean(true)
            PrimitiveType.CHAR -> builder.irChar('z')
            PrimitiveType.BYTE -> 1.toByte().toIrConst(pluginContext.irBuiltIns.byteType)
            PrimitiveType.SHORT -> 123.toShort().toIrConst(pluginContext.irBuiltIns.shortType)
            PrimitiveType.INT -> builder.irInt(888)
            PrimitiveType.FLOAT -> 333f.toIrConst(pluginContext.irBuiltIns.floatType)
            PrimitiveType.LONG -> builder.irLong(123L)
            PrimitiveType.DOUBLE -> 999.0.toIrConst(pluginContext.irBuiltIns.doubleType)
            else -> {
                TODO()
            }
        }
        return rt
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
        val printlnCallId = Standards.printlnCallId
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
