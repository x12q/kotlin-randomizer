package com.x12q.randomizer.ir_plugin.backend.transformers.type_param

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter

class RandomFunctionTypeMap(
    randomFunctionTypes:List<IrTypeParameter>,
    classTypes:List<IrTypeParameter>,
) {
    val typeMap = classTypes.withIndex().map { (i,typeParam)->
        typeParam to randomFunctionTypes[i]
    }.toMap()

    val randomFunctionTypeList = typeMap.values.toList()
    val classTypeList = typeMap.keys.toList()

    companion object{
        val emptyTODO by lazy {
            RandomFunctionTypeMap(emptyList(),emptyList())
        }
    }
}
