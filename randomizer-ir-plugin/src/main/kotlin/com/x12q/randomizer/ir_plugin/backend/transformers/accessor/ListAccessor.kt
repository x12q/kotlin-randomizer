package com.x12q.randomizer.ir_plugin.backend.transformers.accessor

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class ListAccessor @Inject constructor(
    val pluginContext: IrPluginContext
): ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(List::class.qualifiedName!!)))) {
            "kotlin.collections.List is not in the class path."
        }
    }

    private val listFunctionName = CallableId(FqName("kotlin.collections"), Name.identifier("List"))

    fun ListFunction(builder:IrBuilderWithScope):IrCall{
        val function = requireNotNull(pluginContext.referenceFunctions(listFunctionName).firstOrNull { function->
            function.owner.valueParameters.let {
                val correctSize = it.size == 2
                correctSize
                // val firstParamIsInt= it.getOrNull(0)?.type == pluginContext.irBuiltIns.intType
                //
                //
                //
                // val secondIsAfunction = it[1].type == pluginContext.irBuiltIns.functionN(2)
                // correctSize && firstParamIsInt && secondIsAfunction
            }
        }){
            "function kotlin.collections.List does not exist."
        }

        return builder.irCall(function)
    }
}
