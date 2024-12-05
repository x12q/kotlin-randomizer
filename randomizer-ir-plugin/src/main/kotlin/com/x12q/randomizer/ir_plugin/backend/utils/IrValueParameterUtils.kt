package com.x12q.randomizer.ir_plugin.backend.utils

import com.x12q.randomizer.ir_plugin.backend.transformers.random_function.GenericTypeMap
import com.x12q.randomizer.ir_plugin.backend.transformers.random_function.TypeParamOrArg
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.classifierOrNull

fun IrValueParameter.getTypeParamsFromClass(): List<IrTypeParameter>? {
    // val rt = (this.type as? IrSimpleType)?.arguments?.mapNotNull { ((it as? IrSimpleType)?.classifier as? IrTypeParameterSymbol)?.owner}
    val rt = this.type.classOrNull?.owner?.typeParameters
    return rt
}

fun IrValueParameter.getTypeArgs(): List<IrTypeArgument>? {
    val rt = (this.type as? IrSimpleType)?.arguments
    return rt
}

/**
 * Extract generic type-param from type-arg of an [IrValueParameter].
 * Type-arg is that is concrete type will be mapped to a null, but not discarded
 */
fun IrValueParameter.getGenericTypeParamFromTypeArgs(): List<TypeParamOrArg> {
    val args = this.getTypeArgs() ?: emptyList()
    val rt = args.map { arg ->
        val typeParam = (arg as? IrSimpleType)?.classifierOrNull?.owner as? IrTypeParameter
        TypeParamOrArg.make(typeParam, arg)
    }
    return rt
}

/**
 * Construct a local type map from a [param].
 * Return null in case it is not
 */
fun makeLocalTypeMap(
    param: IrValueParameter,
): GenericTypeMap? {
    val typeParam_from_ParamClass = param.getTypeParamsFromClass()
    if (typeParam_from_ParamClass != null) {
        val typeParamOrArgList: List<TypeParamOrArg> = param.getGenericTypeParamFromTypeArgs()

        val localTypeMap = GenericTypeMap.make2(
            keyList = typeParam_from_ParamClass,
            valueList = typeParamOrArgList
        )
        return localTypeMap

    } else {
        // no type param from the class of the param -> no need to construct local type map
        return null
    }
}



/**
 * Check an [IrValueParameter]
 */
fun IrValueParameter.isGeneric(): Boolean{
    val classifier = this.type.classifierOrNull
    return classifier is IrTypeParameterSymbol
}


fun IrValueParameter.getTypeParamFromGenericParam(): IrTypeParameter?{
    if(this.isGeneric()){
        return this.type.classifierOrNull?.owner as? IrTypeParameter
    }else{
        return null
    }
}
