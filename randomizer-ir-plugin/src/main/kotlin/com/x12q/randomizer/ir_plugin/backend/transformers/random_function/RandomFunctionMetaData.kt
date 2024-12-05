package com.x12q.randomizer.ir_plugin.backend.transformers.random_function

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter

class RandomFunctionMetaData(
    /**
     * mapping from [targetClass] type param -> random function type params
     */
    val initTypeMap: GenericTypeMap,
    /**
     * target class is the type that is returned by the random function
     */
    val targetClass: IrClass,
    val companionObj: IrClass,
) {
    // TODO this look bad but it is actually safe
    val randomFunctionTypeList:List<IrTypeParameter> = initTypeMap.tm.values.map { it.getTypeParamOrNull()!! }
    val classTypeList:List<IrTypeParameter> = initTypeMap.tm.keys.toList()

    companion object {
        fun make(
            classTypes: List<IrTypeParameter>,
            randomFunctionTypes: List<IrTypeParameter>,
            targetClass: IrClass,
            companionObj: IrClass,
        ): RandomFunctionMetaData {
            return RandomFunctionMetaData(
                initTypeMap = GenericTypeMap.make(classTypes,randomFunctionTypes),
                targetClass = targetClass,
                companionObj = companionObj
            )
        }
    }
}
