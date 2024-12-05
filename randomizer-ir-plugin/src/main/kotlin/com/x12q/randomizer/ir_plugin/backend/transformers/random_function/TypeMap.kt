package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * A mapping of some type to original class type param
 */
class TypeMap(
    val tm: Map<IrTypeParameter, TypeParamOrArg>
) {
    /**
     * replace VALUES inside [tm] with whatever can be found in [anotherTypeMapX] using the VALUEs inside [tm] as key
     */
    fun bridgeTypeValue(anotherTypeMapX: TypeMap): TypeMap {
        val prevTypeMap: TypeMap = anotherTypeMapX
        val localTypeMap: TypeMap = this

        val newMap = localTypeMap.tm.mapNotNull { (k, v) ->
            val typeParam_of_v = v.getTypeParamOrNull()
            val newV = prevTypeMap.tm[typeParam_of_v]
            if (newV != null) {
                k to newV
            } else {
                // keep intact in case cannot find a new target for k
                k to v
            }
        }.toMap()
        val rt = TypeMap(newMap)
        return rt
    }

    companion object {

        val emptyTODO = TypeMap(emptyMap())

        val empty = TypeMap(emptyMap())

        fun make(
            keyList: List<IrTypeParameter>,
            valueList: List<IrTypeParameter>
        ): TypeMap {

            val tm = keyList.zip(valueList)
                .map { (k, v) -> k to TypeParamOrArg.Param(v,null) }
                .toMap()
            return TypeMap(
                tm = tm
            )
        }

        fun make2(
            keyList: List<IrTypeParameter>,
            valueList: List<TypeParamOrArg>
        ): TypeMap {
            val tm = keyList.zip(valueList).toMap()
            return TypeMap(
                tm = tm
            )
        }
    }
}
