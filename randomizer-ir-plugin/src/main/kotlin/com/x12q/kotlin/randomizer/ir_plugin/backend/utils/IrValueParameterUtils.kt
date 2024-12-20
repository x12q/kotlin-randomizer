package com.x12q.kotlin.randomizer.ir_plugin.backend.utils

import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.support.TypeMap
import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.support.TypeParamOrArg
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.classifierOrNull

/**
 * Attempt to get a list of type parameter from class of a particular param.
 * When the param is not backed by a class (such as when it backed by a generic), this will return null.
 */
fun IrValueParameter.getTypeParamsFromClass(): List<IrTypeParameter>? {
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
 * Construct a [TypeMap] from a [IrValueParameter].
 */
fun IrValueParameter.makeTypeMap(): TypeMap {

    /**
     * type params is gotten from the class backing the [IrValueParamter]
     * type args are gotten from the type obj backing the [IrValueParamter]
     */

    val param = this
    val typeParam_from_ParamClass = param.getTypeParamsFromClass()
    if (typeParam_from_ParamClass != null) {
        val typeParamOrArgList: List<TypeParamOrArg> = param.getGenericTypeParamFromTypeArgs()

        val localTypeMap = TypeMap.make(
            typeParams = typeParam_from_ParamClass,
            valueList = typeParamOrArgList
        )
        return localTypeMap

    } else {
        // no type param from the class of the param -> no need to construct local type map
        return TypeMap.empty
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
