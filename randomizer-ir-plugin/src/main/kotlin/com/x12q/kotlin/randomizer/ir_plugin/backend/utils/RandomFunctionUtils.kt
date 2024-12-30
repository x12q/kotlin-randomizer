package com.x12q.kotlin.randomizer.ir_plugin.backend.utils

import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.hasEqualFqName

fun isRandomFunctions(
    function: IrFunction,
    randomContextBuilderType: IrType
): Boolean{
    val c1 = isStandAloneRandomFunctions(function)
    val c2 = isRandomFunctionForRdContextBuilder(function,randomContextBuilderType)
    return c1 || c2
}

fun isStandAloneRandomFunctions(
    function: IrFunction
): Boolean{
    val correctNameAndPackage = function.hasEqualFqName(BaseObjects.IndependentRandomFunction.fullFqName)
    // TODO this check is good enough for now, but it may need to be strengthen a bit more.
    val rightNumberOfArg = function.valueParameters.size == 3
    val noExtensionReceiver = function.extensionReceiverParameter == null
    return correctNameAndPackage && (rightNumberOfArg) && noExtensionReceiver
}


fun isRandomFunctionForRdContextBuilder(
    function: IrFunction,
    randomContextBuilderType: IrType
): Boolean{
    val correctNameAndPackage = function.hasEqualFqName(BaseObjects.IndependentRandomFunction.fullFqName)
    // TODO this check is good enough for now, but it may need to be strengthen a bit more.
    val rightNumberOfArg = function.valueParameters.size == 3
    val isExtensionFunctionofRandomContextBuilder = function.extensionReceiverParameter?.type == randomContextBuilderType
    return correctNameAndPackage && rightNumberOfArg && isExtensionFunctionofRandomContextBuilder
}

fun IrFunction.getMakeRandomParam(): IrValueParameter?{
    return this.valueParameters.firstOrNull { valueParam->
        valueParam.name == BaseObjects.IndependentRandomFunction.makeRandomParamName
    }
}

fun IrFunction.getRandomizersParam(): IrValueParameter?{
    return this.valueParameters.firstOrNull { valueParam->
        valueParam.name == BaseObjects.IndependentRandomFunction.randomizersParamName
    }
}

fun IrCall.getArgAtParam(parameter: IrValueParameter): IrExpression? {
    val irCall = this
    val rt= irCall.getValueArgument(parameter.index)
    return rt
}

fun IrFunctionAccessExpression.getArgAtParam(parameter: IrValueParameter): IrExpression? {
    val irCall = this
    val rt= irCall.getValueArgument(parameter.index)
    return rt
}
