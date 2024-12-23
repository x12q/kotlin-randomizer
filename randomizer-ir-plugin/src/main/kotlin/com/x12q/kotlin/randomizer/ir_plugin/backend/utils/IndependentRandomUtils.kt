package com.x12q.kotlin.randomizer.ir_plugin.backend.utils

import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import com.x12q.kotlin.randomizer.lib.RandomContextBuilder
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.hasEqualFqName

fun isRandomFunctions(
    function: IrSimpleFunction,
    randomContextBuilderType: IrType
): Boolean{
    return isStandAloneRandomFunctions(function) || isRandomFunctionForRdContextBuilder(function,randomContextBuilderType)
}

fun isStandAloneRandomFunctions(
    function: IrSimpleFunction
): Boolean{
    val correctNameAndPackage = function.hasEqualFqName(BaseObjects.IndependentRandomFunction.fullFqName)
    // TODO this check is good enough for now, but it may need to be strengthen a bit more.
    val isRandomIndie1 = function.valueParameters.size == 3
    return correctNameAndPackage && (isRandomIndie1)
}


fun isRandomFunctionForRdContextBuilder(
    function: IrSimpleFunction,
    randomContextBuilderType: IrType
): Boolean{
    val correctNameAndPackage = function.hasEqualFqName(BaseObjects.IndependentRandomFunction.fullFqName)
    // TODO this check is good enough for now, but it may need to be strengthen a bit more.
    val isRandomIndie1 = function.valueParameters.size == 3
    val isExtensionFunctionofRandomContextBuilder = function.extensionReceiverParameter?.type == randomContextBuilderType
    return correctNameAndPackage && isRandomIndie1 && isExtensionFunctionofRandomContextBuilder
}

fun IrSimpleFunction.getMakeRandomParam(): IrValueParameter?{
    return this.valueParameters.firstOrNull { valueParam->
        valueParam.name == BaseObjects.IndependentRandomFunction.makeRandomParamName
    }
}

fun IrSimpleFunction.getRandomizersParam(): IrValueParameter?{
    return this.valueParameters.firstOrNull { valueParam->
        valueParam.name == BaseObjects.IndependentRandomFunction.randomizersParamName
    }
}

fun IrCall.getArgAtParam(parameter: IrValueParameter): IrExpression? {
    val irCall = this
    val rt= irCall.getValueArgument(parameter.index)
    return rt
}
