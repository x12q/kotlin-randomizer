package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * A mapping of type parameters of a **class** to some other type parameters or type arguments
 */
class ClassTypeMap(
    val tm: Map<IrTypeParameter, TypeParamOrArg>
) {
    /**
     * replace VALUES inside [tm] with whatever can be found in [anotherTypeMapX] using the VALUEs inside [tm] as key
     */
    fun bridgeTypeValue(anotherTypeMapX: ClassTypeMap): ClassTypeMap {
        val prevTypeMap: ClassTypeMap = anotherTypeMapX
        val localTypeMap: ClassTypeMap = this

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
        val rt = ClassTypeMap(newMap)
        return rt
    }

    companion object {

        val emptyTODO = ClassTypeMap(emptyMap())

        val empty = ClassTypeMap(emptyMap())

        fun make(
            keyList: List<IrTypeParameter>,
            valueList: List<IrTypeParameter>
        ): ClassTypeMap {

            val tm = keyList.zip(valueList)
                .map { (k, v) -> k to TypeParamOrArg.Param(v,null) }
                .toMap()
            return ClassTypeMap(
                tm = tm
            )
        }

        fun make2(
            keyList: List<IrTypeParameter>,
            valueList: List<TypeParamOrArg>
        ): ClassTypeMap {
            val tm = keyList.zip(valueList).toMap()
            return ClassTypeMap(
                tm = tm
            )
        }
    }
}
