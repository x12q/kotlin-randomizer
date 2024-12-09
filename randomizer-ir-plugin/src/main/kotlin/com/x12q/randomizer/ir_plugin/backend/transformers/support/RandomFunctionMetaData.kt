package com.x12q.randomizer.ir_plugin.backend.transformers.support

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter

class RandomFunctionMetaData(
    /**
     * mapping from [targetClass] type param -> random function type params
     */
    val initTypeMap: TypeMap,
    /**
     * target class is the type that is returned by the random function
     */
    val targetClass: IrClass,
    val companionObj: IrClass,
) {
    companion object {
        fun make(
            classTypes: List<IrTypeParameter>,
            randomFunctionTypes: List<IrTypeParameter>,
            targetClass: IrClass,
            companionObj: IrClass,
        ): RandomFunctionMetaData {
            return RandomFunctionMetaData(
                initTypeMap = TypeMap.makeFromTypeParams(classTypes,randomFunctionTypes),
                targetClass = targetClass,
                companionObj = companionObj
            )
        }
    }
}
