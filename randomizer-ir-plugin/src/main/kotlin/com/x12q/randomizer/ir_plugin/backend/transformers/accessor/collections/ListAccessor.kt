package com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections

import com.x12q.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.randomizer.ir_plugin.util.crashOnNull
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
): ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(List::class.qualifiedName!!)))) {
            "kotlin.collections.List is not in the class path."
        }
    }

    private val makeListFunctionName = CallableId(FqName("com.x12q.randomizer.lib.util"), Name.identifier("makeList"))

    /**
     * Get a reference to kotlin.to function.
     */
    fun makeListFunctionCall(builder: IrBuilderWithScope):IrCall{
        val bmFunction = pluginContext.referenceFunctions(makeListFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections.makeList does not exist."
            }

        return builder.irCall(bmFunction)
    }
}
