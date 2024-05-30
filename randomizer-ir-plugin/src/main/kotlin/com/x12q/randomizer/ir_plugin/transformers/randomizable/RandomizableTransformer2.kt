package com.x12q.randomizer.ir_plugin.transformers.randomizable

import com.x12q.randomizer.annotations.Randomizer
import com.x12q.randomizer.ir_plugin.transformers.randomizable.utils.isAnnotatedWith
import com.x12q.randomizer.ir_plugin.transformers.utils.Standards
import org.jetbrains.kotlin.backend.common.ClassLoweringPass
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject


/**
 *  Road map:
 *  - check:
 *      - can access constructor: ok, but how
 *
 * Strategy:
 *  - scan for annotated class, then generate a static function generate random
 *
 */
class RandomizableTransformer2 @Inject constructor(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {

    private val `@Randomizable` = BasicObjects.randomizableAnnotation

    private val dumpBuilder: StringBuilder = StringBuilder()


    /**
     * Visit class new is the alternative to visit class. Used in *WithContext visitor
     */
    override fun visitClassNew(declaration: IrClass): IrStatement {
        val irClass = declaration
        val name = irClass.name.toString()
        if (irClass.companionObject() != null) {
            dumpBuilder.appendLine("$name $currentClass yes")
        } else {
            dumpBuilder.appendLine("$name ${currentClass?.irElement?.dumpToDump()} no")
        }
        return super.visitClassNew(declaration)
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
