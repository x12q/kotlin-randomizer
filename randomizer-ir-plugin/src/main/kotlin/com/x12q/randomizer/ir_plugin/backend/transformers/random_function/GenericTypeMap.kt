package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import com.x12q.randomizer.ir_plugin.backend.transformers.reporting.developerErrorMsg
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * A mapping of some type to original class type param
 */
class GenericTypeMap(
    val tm: Map<IrTypeParameter, IrTypeParameter>,
) {
    /**
     * replace VALUES inside [tm] with whatever can be found in [anotherTypeMapX] using the VALUEs inside [tm] as key
     */
    fun bridgeTypeValue(anotherTypeMapX: GenericTypeMap): GenericTypeMap {
        val prevTypeMap: GenericTypeMap = anotherTypeMapX
        val localTypeMap: GenericTypeMap = this

        val newMap = localTypeMap.tm.mapNotNull { (k, v) ->
            val newV = prevTypeMap.tm[v]
            if (newV != null) {
                k to newV
            } else {
                throw IllegalArgumentException(developerErrorMsg("Illegal type map"))
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
            valueList: List<IrTypeParameter?>
        ): GenericTypeMap {

            val tm = keyList.zip(valueList)
                .filter { (_, v) -> v != null }
                .map { (k, v) -> k to v!! }
                .toMap()
            return GenericTypeMap(
                tm = tm
            )
        }
    }
}
