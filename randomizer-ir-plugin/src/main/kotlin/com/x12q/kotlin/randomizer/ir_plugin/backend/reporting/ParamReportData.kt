package com.x12q.kotlin.randomizer.ir_plugin.backend.reporting

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

/**
 * A class to hold information to construct a meaningful exception message.
 */
data class ParamReportData(
    val paramName: String?,
    val paramType: String,
    val clazzName: String?,
) : ReportData {
    companion object {
        private const val errCode = "PARAM_ERR_1"
        fun fromIrElements(
            param: IrValueParameter?,
            irType: IrType,
            enclosingClass: IrClass?
        ): ParamReportData {
            return ParamReportData(
                paramName = param?.name?.asString(),
                paramType = irType.dumpKotlinLike(),
                clazzName = enclosingClass?.name?.asString()
            )
        }
    }

    override fun makeMsg(): String {
        return """
    $errCode: Unable to generate random for:
        class: $clazzName
        paramName: $paramName
        type: $paramType
    """.trimIndent()
    }
}
