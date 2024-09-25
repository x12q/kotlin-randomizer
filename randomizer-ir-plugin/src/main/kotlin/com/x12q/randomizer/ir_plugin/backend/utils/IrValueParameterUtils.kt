package com.x12q.randomizer.ir_plugin.backend.utils

import com.x12q.randomizer.ir_plugin.backend.transformers.ParamMetaDataForReporting
import org.jetbrains.kotlin.ir.backend.js.utils.asString
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

fun IrValueParameter.makeReportData(enclosingClassName:String?): ParamMetaDataForReporting{
    return ParamMetaDataForReporting(
        paramName = this.name.asString(),
        paramType = this.type.dumpKotlinLike(),
        clazzName = enclosingClassName,
    )
}