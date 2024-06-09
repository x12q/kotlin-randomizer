package com.x12q.randomizer.ir_plugin.backend.transformers.randomizable

import com.x12q.randomizer.ir_plugin.frontend.k2.base.BaseObjects
import com.x12q.randomizer.ir_plugin.backend.transformers.utils.Standards
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.codegen.inline.addFakeContinuationConstructorCallMarker
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrDelegatingConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class RandomizableTransformer2 @Inject constructor(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {

    private val `@Randomizable` = BaseObjects.randomizableFqName

    private val dumpBuilder: StringBuilder = StringBuilder()

    val irFactory = pluginContext.irFactory

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if(declaration.name.toString().contains("Q123")){
            val irClass = declaration
            val companionObj = irClass.companionObject()
            if (companionObj != null) {
                addRandomFunction(companionObj)
            }
        }
        return super.visitClassNew(declaration)
    }

    /**
     * Add random() function to [companionObj]
     */
    fun addRandomFunction(companionObj: IrClass) {
        if(true){
            companionObj.addFunction {
                val builder = this
                builder.name = BaseObjects.randomFunctionName
                builder.origin = BaseObjects.irDeclarationOrigin
                builder.visibility = DescriptorVisibilities.PUBLIC
                builder.returnType = pluginContext.irBuiltIns.unitType
                builder.modality = Modality.FINAL
                builder.isSuspend = false
            }.apply {
                val func = this
                val builder = DeclarationIrBuilder(
                    generatorContext = pluginContext,
                    symbol = this.symbol,
                )
                body = builder.irBlockBody {
                    +printDumpCall(
                        pluginContext, func, this
                    )
                }
            }

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