package com.x12q.randomizer.ir_plugin.backend.utils

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.hasEqualFqName

fun isIndependentRandomFunction(
    function: IrSimpleFunction
): Boolean{
    val correctNameAndPackage = function.hasEqualFqName(BaseObjects.IndependentRandomFunction.fullFqName)
    // TODO this check is good enough for now, but it may need to be strengthen a bit more.
    val isRandomIndie1 = function.valueParameters.size == 3
    val isRandomIndie2 = function.valueParameters.size == 2
    return correctNameAndPackage && (isRandomIndie1 || isRandomIndie2)
}

fun IrSimpleFunction.getMakeRandomValueParam(): IrValueParameter?{
    return this.valueParameters.firstOrNull { valueParam->
        valueParam.name == BaseObjects.IndependentRandomFunction.makeRandomParamName
    }
}

fun IrCall.getMakeRandomArg(makeRandomParameter: IrValueParameter): IrExpression? {
    val irCall = this
    val rt= irCall.getValueArgument(makeRandomParameter.index)
    return rt
}
