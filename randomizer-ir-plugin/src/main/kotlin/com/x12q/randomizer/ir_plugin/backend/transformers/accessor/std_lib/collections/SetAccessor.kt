package com.x12q.randomizer.ir_plugin.backend.transformers.accessor.std_lib.collections

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

class SetAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Set::class.qualifiedName!!)))) {
            "kotlin.collections.Set is not in the class path."
        }
    }

    private val listToSetFunctionSymbol by lazy {
        val listToSetFunctionName = CallableId(FqName("com.x12q.randomizer.lib.util"), Name.identifier("listToSet"))
        pluginContext.referenceFunctions(listToSetFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections.listToSet does not exist."
            }
    }

    /**
     * Get a reference to kotlin.to function.
     */
    fun listToSetFunctionCall(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(listToSetFunctionSymbol)
    }
}
