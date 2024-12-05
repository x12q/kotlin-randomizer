package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import com.x12q.randomizer.ir_plugin.backend.transformers.reporting.developerErrorMsg
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * A mapping of some type to original class type param
 */
class GenericTypeMap(
    val tm: Map<IrTypeParameter, TypeParamOrArg>
) {
    /**
     * replace VALUES inside [tm] with whatever can be found in [anotherTypeMapX] using the VALUEs inside [tm] as key
     */
    fun bridgeTypeValue(anotherTypeMapX: GenericTypeMap): GenericTypeMap {
        val prevTypeMap: GenericTypeMap = anotherTypeMapX
        val localTypeMap: GenericTypeMap = this

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
        val rt = GenericTypeMap(newMap)
        return rt
    }

    companion object {

        val emptyTODO = GenericTypeMap(emptyMap())

        val empty = GenericTypeMap(emptyMap())

        fun make(
            keyList: List<IrTypeParameter>,
            valueList: List<IrTypeParameter>
        ): GenericTypeMap {

            val tm = keyList.zip(valueList)
                .map { (k, v) -> k to TypeParamOrArg.Param(v,null) }
                .toMap()
            return GenericTypeMap(
                tm = tm
            )
        }

        fun make2(
            keyList: List<IrTypeParameter>,
            valueList: List<TypeParamOrArg>
        ): GenericTypeMap {
            val tm = keyList.zip(valueList).toMap()
            return GenericTypeMap(
                tm = tm
            )
        }
    }
}
