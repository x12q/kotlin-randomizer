package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.types.IrTypeArgument

sealed class TypeParamOrArg {

    data class Param(val typeParam: IrTypeParameter, val typeArg: IrTypeArgument?) : TypeParamOrArg() {
        override fun getTypeParamOrNull(): IrTypeParameter? {
            return typeParam
        }
    }

    data class Arg(val typeArg: IrTypeArgument) : TypeParamOrArg() {
        override fun getTypeParamOrNull(): IrTypeParameter? = null
    }

    abstract fun getTypeParamOrNull(): IrTypeParameter?

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
