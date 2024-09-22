package com.x12q.randomizer.ir_plugin.backend.transformers

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

/**
 * A class to hold information to construct a meaningful exception message.
 */
data class ParamMetaDataForReporting(
    val paramName:String?,
    val paramType:String,
    val clazzName:String?,
){
    companion object{
        fun fromIrElements(
            param:IrValueParameter?,
            irType: IrType,
            enclosingClass:IrClass?
        ):ParamMetaDataForReporting{
            return ParamMetaDataForReporting(
                paramName = param?.name?.asString(),
                paramType = irType.dumpKotlinLike(),
                clazzName = enclosingClass?.name?.asString()
            )
        }
    }
}
