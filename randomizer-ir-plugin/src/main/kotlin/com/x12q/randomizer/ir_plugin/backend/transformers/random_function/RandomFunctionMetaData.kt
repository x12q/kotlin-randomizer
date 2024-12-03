package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter

class RandomFunctionMetaData(
    val typeMap: Map<IrTypeParameter, IrTypeParameter>,
    val parentClass: IrClass,
    val companionObj: IrClass,
) {
    val randomFunctionTypeList = typeMap.values.toList()
    val classTypeList = typeMap.keys.toList()

    companion object {
        fun make(
            randomFunctionTypes: List<IrTypeParameter>,
            classTypes: List<IrTypeParameter>,
            parentClass: IrClass,
            companionObj: IrClass,
        ): RandomFunctionMetaData {
            return RandomFunctionMetaData(
                typeMap = classTypes.withIndex().map { (i, typeParam) ->
                    typeParam to randomFunctionTypes[i]
                }.toMap(),
                parentClass = parentClass,
                companionObj = companionObj
            )
        }
    }
}
