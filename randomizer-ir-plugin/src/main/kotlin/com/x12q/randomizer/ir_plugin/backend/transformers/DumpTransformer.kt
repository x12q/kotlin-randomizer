package com.x12q.randomizer.ir_plugin.backend.transformers

import com.x12q.randomizer.ir_plugin.backend.transformers.utils.Standards
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * A transformer that simply add a println() to the beginning of any functino named "someFunction" to print the dump of that function itself.
 */
class DumpTransformer(
    private val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {
    val serializableName = FqName("kotlinx.serialization.Serializable")
    val classDataBuilder: StringBuilder = StringBuilder()
    override fun visitClassNew(declaration: IrClass): IrStatement {
        val isABC = declaration.name.identifier == "ABC"
        val isClass = declaration.isClass
        if (isABC && isClass) {


//
//            val isAnnotatedWithComposable = irCall.symbol.owner.annotations.any {
//                it.symbol.owner.parentAsClass.symbol == composableAnnotation
//            }

            /**
             * Annotation is created using their special constructor. So the list of annotation is actually a list of constructor calls.
             */
            val isAnnotatedWithRandomizable = declaration.annotations.forEach { constructorCall ->

                if (constructorCall.isAnnotation(serializableName)) {

                    classDataBuilder.appendLine(
                        constructorCall.symbol
                    )
                        .appendLine(
                            constructorCall
                                .symbol.owner
                                .parent
                                .dump()

                        )
                }
            }
        }



        return super.visitClassNew(declaration)
    }

    /**
     * Alter the body of [declaration], so that a println statement that prints out the dump of the function itself is added at the beginning of the function body.
     */
    override fun visitFunctionNew(declaration: IrFunction): IrStatement {

        val oldBody = declaration.body

        if (oldBody != null && isSomeFunction(declaration)) {
            val declarationBuilder = DeclarationIrBuilder(
                generatorContext = pluginContext,
                symbol = declaration.symbol
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

    /**
     * Make an [IrCall] that call println that prints out the dump of [targetFunction]
     */
    private fun printDumpCall(
        pluginContext: IrPluginContext,
        targetFunction: IrFunction,
        irBuilder: IrBuilderWithScope,
    ): IrCall {
//        val printlnCallId = CallableId(FqName("kotlin.io"),Name.identifier("println"))
        val printlnCallId = Standards.printlnCallId
        val strIR = irBuilder.irString(
//            targetFunction.dump()
            classDataBuilder.toString()
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
