package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import javax.inject.Inject


/**
 *  Road map:
 *  - check:
 *      - can access constructor: ok, but how
 *
 * Strategy:
 *  - scan for special function call, and change the lambda of that function.
 *
 */
class BenchTransformer @Inject constructor(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {

    private val randomFunctionName = FqName("com.x12q.randomizer.sample_app.makeRandomInstance")

    private val dumpBuilder: StringBuilder = StringBuilder()

    /**
     * Example of generating a lambda to generate a random ABC class
     */
    override fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrExpression {
        if (expression.symbol.owner.fqNameWhenAvailable == randomFunctionName) {
            val targetType = expression.typeArguments.firstOrNull()
            if(targetType!=null){
                val clazz = targetType.classOrNull
                if(clazz!=null){
                    val lambda = pluginContext.irFactory.buildFun {
                        name = SpecialNames.ANONYMOUS
                        origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                        visibility = DescriptorVisibilities.LOCAL
                        returnType = targetType
                        modality = Modality.FINAL
                        isSuspend = false
                    }.apply {
                        val builder = DeclarationIrBuilder(
                            generatorContext = pluginContext,
                            symbol = this.symbol,
                        )

                        val clazz = targetType.classOrNull!!
                        val constructorSymbol = clazz.constructors.first { it.owner.visibility == DescriptorVisibilities.PUBLIC }
                        val constructor = constructorSymbol.owner

                        val argTypes = constructor.valueParameters.map {arg->
                            val type = arg.type

                            val primType = type.getPrimitiveType()
                            when(primType){
                                PrimitiveType.BOOLEAN -> TODO()
                                PrimitiveType.CHAR -> TODO()
                                PrimitiveType.BYTE -> TODO()
                                PrimitiveType.SHORT -> TODO()
                                PrimitiveType.INT -> builder.irInt(12333)
                                PrimitiveType.FLOAT -> TODO()
                                PrimitiveType.LONG -> TODO()
                                PrimitiveType.DOUBLE -> TODO()
                                else -> {
                                    if(type == pluginContext.irBuiltIns.stringType){
                                        builder.irString("UUID: str")
                                    }else{
                                        TODO()
                                    }
                                }
                            }
                        }

                        body = builder.irBlockBody {
                            +builder.irReturn(builder.irCall(constructorSymbol).also {con->
                                argTypes.withIndex().forEach {(index,a)->
                                    con.putValueArgument(index,a)
                                }
                            })
                        }
                    }

                    val newLambda = IrFunctionExpressionImpl(
                        startOffset = lambda.startOffset,
                        endOffset = lambda.endOffset,
                        type = pluginContext.irBuiltIns.functionN(0).typeWith(targetType),
                        function = lambda,
                        origin = IrStatementOrigin.LAMBDA
                    )
                    expression.putValueArgument(0, newLambda)
                }
            }
        }
        return super.visitFunctionAccess(expression)
    }

    override fun visitClassNew(declaration: IrClass): IrStatement {
        return super.visitClassNew(declaration)
    }

    override fun visitValueParameterNew(declaration: IrValueParameter): IrStatement {
        return super.visitValueParameterNew(declaration)
    }

    fun IrElement.dumpToDump() {
        dumpBuilder.appendLine(
            this.dump()
        )
    }

    /**
     * visit generic type parameter <T>
     */
    override fun visitTypeParameter(declaration: IrTypeParameter): IrStatement {
        return super.visitTypeParameter(declaration)
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
