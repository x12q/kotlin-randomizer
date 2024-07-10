package com.x12q.randomizer.ir_plugin.backend.utils

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol

class UtilIRBuilderImp(
    private val pluginContext: IrPluginContext,
    private val irBuilder: IrBuilderWithScope,
) : UtilIRBuilder {

    override fun printlnIr(message: IrExpression): IrCall? {
        val printlnIr = findPrintlnIrFunctionSymbol()
        val rt: IrCall? = printlnIr?.let {
            irBuilder.irCall(it).apply {
                this.putValueArgument(0, message)
            }
        }
        return rt
    }

    private fun findPrintlnIrFunctionSymbol():IrSimpleFunctionSymbol?{
        val printlnCallId = BaseObjects.Std.printlnCallId
        val anyType = pluginContext.irBuiltIns.anyNType

        val printlnIr =   pluginContext.referenceFunctions(printlnCallId).firstOrNull {funSymbol: IrSimpleFunctionSymbol ->
            funSymbol.owner.valueParameters.let {parameters: List<IrValueParameter> ->
                parameters.size == 1 && parameters[0].type == anyType
            }
        }
        return printlnIr
    }

    override fun printlnIr(message: String): IrCall? {
        val arg: IrExpression = irBuilder.irString(message)
        return printlnIr(arg)
    }

    override fun printlnIr(message: Any?): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: Boolean): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: Byte): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: Char): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: CharArray): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: Double): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: Float): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: Int): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: Long): IrCall? {
        return this.printlnIr(message.toString())
    }

    override fun printlnIr(message: Short): IrCall? {
        return this.printlnIr(message.toString())
    }

}
