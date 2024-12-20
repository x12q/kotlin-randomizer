package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.support

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

sealed class TypeParamOrArg {

    abstract fun getTypeParamOrNull(): IrTypeParameter?
    abstract fun getTypeArgOrNull(): IrTypeArgument?
    abstract fun getIrTypeOrNull(): IrType?

    data class Param(val typeParam: IrTypeParameter, val typeArg: IrTypeArgument?) : TypeParamOrArg() {
        override fun getTypeParamOrNull(): IrTypeParameter? = typeParam
        override fun getTypeArgOrNull(): IrTypeArgument? = typeArg
        override fun getIrTypeOrNull(): IrType {
            return typeParam.defaultType
        }

        override fun toString(): String {
            return typeParam.dumpKotlinLike()
        }
    }

    data class Arg(val typeArg: IrTypeArgument) : TypeParamOrArg() {
        override fun getTypeParamOrNull(): IrTypeParameter? = (typeArg.typeOrNull?.classifierOrNull as? IrTypeParameterSymbol)?.owner
        override fun getTypeArgOrNull(): IrTypeArgument? = typeArg
        override fun getIrTypeOrNull(): IrType? {
            return typeArg.typeOrNull
        }
        override fun toString(): String {
            return typeArg.dumpKotlinLike()
        }
    }

    companion object {
        fun make(typeParam: IrTypeParameter?, typeArg: IrTypeArgument): TypeParamOrArg {
            if (typeParam != null) {
                return Param(typeParam, typeArg)
            } else {
                return Arg(typeArg)
            }
        }
    }
}
