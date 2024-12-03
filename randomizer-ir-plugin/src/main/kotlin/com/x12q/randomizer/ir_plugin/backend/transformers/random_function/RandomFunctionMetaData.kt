package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter

class RandomFunctionMetaData(
    val classTypeToRandomFunctionTypeMap: Map<IrTypeParameter, IrTypeParameter>,
    val targetClass: IrClass,
    val companionObj: IrClass,
) {
    val randomFunctionTypeList = classTypeToRandomFunctionTypeMap.values.toList()
    val classTypeList = classTypeToRandomFunctionTypeMap.keys.toList()

    companion object {
        fun make(
            randomFunctionTypes: List<IrTypeParameter>,
            classTypes: List<IrTypeParameter>,
            /**
             * target class is the type that is returned by the random function
             */
            targetClass: IrClass,
            companionObj: IrClass,
        ): RandomFunctionMetaData {
            return RandomFunctionMetaData(
                classTypeToRandomFunctionTypeMap = classTypes.withIndex().map { (i, typeParam) ->
                    typeParam to randomFunctionTypes[i]
                }.toMap(),
                targetClass = targetClass,
                companionObj = companionObj
            )
        }
    }
}
