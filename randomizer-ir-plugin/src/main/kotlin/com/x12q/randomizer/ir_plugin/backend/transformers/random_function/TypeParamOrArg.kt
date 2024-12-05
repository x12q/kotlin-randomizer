package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeOrNull

sealed class TypeParamOrArg {

    data class Param(val typeParam: IrTypeParameter, val typeArg: IrTypeArgument?) : TypeParamOrArg() {
        override fun getTypeParamOrNull(): IrTypeParameter? = typeParam
        override fun getTypeArgOrNull(): IrTypeArgument? = typeArg
        override fun getIrTypeOrNull(): IrType {
            return typeParam.defaultType
        }
    }

    data class Arg(val typeArg: IrTypeArgument) : TypeParamOrArg() {
        override fun getTypeParamOrNull(): IrTypeParameter? = null
        override fun getTypeArgOrNull(): IrTypeArgument? = typeArg
        override fun getIrTypeOrNull(): IrType? {
            return typeArg.typeOrNull
        }
    }

    abstract fun getTypeParamOrNull(): IrTypeParameter?
    abstract fun getTypeArgOrNull(): IrTypeArgument?
    abstract fun getIrTypeOrNull(): IrType?


    companion object {
        fun make(typeParam: IrTypeParameter?, typeArg: IrTypeArgument): TypeParamOrArg {
            if (typeParam != null) {
                return Param(typeParam, typeArg)
            } else {
                return Arg(typeArg)
            }
        }

        fun extractParam(l: List<TypeParamOrArg>): List<IrTypeParameter?> {
            val rt = l.map { q: TypeParamOrArg ->
                when (q) {
                    is Arg -> null
                    is Param -> q.typeParam
                }
            }
            return rt
        }
    }
}
