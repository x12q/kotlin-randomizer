package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.std_lib.collections

import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class ListAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(List::class.qualifiedName!!)))) {
            "kotlin.collections.List is not in the class path."
        }
    }

    private val makeListFunctionSymbol by lazy {
        val makeListFunctionName = CallableId(FqName("com.x12q.kotlin.randomizer.lib.util"), Name.identifier("makeList"))
        pluginContext.referenceFunctions(makeListFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.lib.util.makeList does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeList]
     */
    fun makeList(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeListFunctionSymbol)
    }

    private val makeArrayListFunctionSymbol by lazy {
        val makeArrayListFunctionName =
            CallableId(FqName("com.x12q.kotlin.randomizer.lib.util"), Name.identifier("makeArrayList"))
        pluginContext.referenceFunctions(makeArrayListFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.lib.util.makeArrayList does not exist."
            }
    }

    fun makeArrayList(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeArrayListFunctionSymbol)
    }
}
