package com.x12q.randomizer.ir_plugin.transformers.randomizable

import com.x12q.randomizer.annotations.Randomizer
import com.x12q.randomizer.ir_plugin.transformers.utils.Standards
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
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import javax.inject.Inject
import kotlin.random.Random

class IrRandomizer @Inject constructor(
    private val pluginContext: IrPluginContext
) {
    private val randomizableName = FqName(Randomizer::class.qualifiedName!!)
    private val randomFunctionName = FqName("com.x12q.randomizer.sample_app.makeRandomInstance")

    /**
     * Example of generating a lambda to generate a random ABC class
     */
    fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrFunctionAccessExpression {
        if (expression.symbol.owner.fqNameWhenAvailable == randomFunctionName) {
            val targetType = expression.typeArguments.firstOrNull()!!

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
        return expression
    }
}
