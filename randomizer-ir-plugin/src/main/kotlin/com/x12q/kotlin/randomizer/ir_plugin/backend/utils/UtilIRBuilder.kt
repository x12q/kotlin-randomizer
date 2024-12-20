package com.x12q.kotlin.randomizer.ir_plugin.backend.utils

import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

/**
 * Provide function to quickly build IR for utility functions such as println
 */
interface UtilIRBuilder {
    fun printlnIr(message: String): IrCall?
    fun printlnIr(message: IrExpression): IrCall?
    fun printlnIr(message: Any?): IrCall?
    fun printlnIr(message: Boolean): IrCall?
    fun printlnIr(message: Byte): IrCall?
    fun printlnIr(message: Char): IrCall?
    fun printlnIr(message: CharArray): IrCall?
    fun printlnIr(message: Double): IrCall?
    fun printlnIr(message: Float): IrCall?
    fun printlnIr(message: Int): IrCall?
    fun printlnIr(message: Long): IrCall?
    fun printlnIr(message: Short): IrCall?
}

