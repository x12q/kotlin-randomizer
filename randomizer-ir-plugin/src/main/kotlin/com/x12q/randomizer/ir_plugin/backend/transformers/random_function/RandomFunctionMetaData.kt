package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter

class RandomFunctionMetaData(
    val typeMap: Map<IrTypeParameter, IrTypeParameter>
) {
    val randomFunctionTypeList = typeMap.values.toList()
    val classTypeList = typeMap.keys.toList()

    companion object {
        val emptyTODO by lazy {
            RandomFunctionMetaData(emptyMap())
        }

        fun make(
            randomFunctionTypes: List<IrTypeParameter>,
            classTypes: List<IrTypeParameter>,
        ): RandomFunctionMetaData {
            return RandomFunctionMetaData(
                typeMap = classTypes.withIndex().map { (i, typeParam) ->
                    typeParam to randomFunctionTypes[i]
                }.toMap()
            )
        }
    }
}
