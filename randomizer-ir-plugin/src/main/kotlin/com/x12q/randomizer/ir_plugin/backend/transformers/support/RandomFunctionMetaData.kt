package com.x12q.randomizer.ir_plugin.backend.transformers.support

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.types.IrTypeArgument

class RandomFunctionMetaData(
    /**
     * mapping from [targetClass] type param -> random function type params
     */
    val initTypeMap: TypeMap,
    /**
     * target class is the type that is returned by the random function
     */
    val targetClass: IrClass,
) {
    companion object {
        fun make(
            classTypeParameters: List<IrTypeParameter>,
            randomFunctionTypeParameters: List<IrTypeParameter>,
            targetClass: IrClass,
        ): RandomFunctionMetaData {
            return RandomFunctionMetaData(
                initTypeMap = TypeMap.makeFromTypeParams(classTypeParameters,randomFunctionTypeParameters),
                targetClass = targetClass,
            )
        }

        fun makeForNewRandFunction(
            classTypeParameters: List<IrTypeParameter>,
            randomFunctionTypeArgs: List<IrTypeArgument>,
            targetClass: IrClass,
        ): RandomFunctionMetaData {

            val typeArgs = randomFunctionTypeArgs.map {
                TypeParamOrArg.Arg(it)
            }
            return RandomFunctionMetaData(
                initTypeMap = TypeMap.make(classTypeParameters,typeArgs),
                targetClass = targetClass,
            )
        }
    }
}
