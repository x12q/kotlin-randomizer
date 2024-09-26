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


class MapAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Map::class.qualifiedName!!)))) {
            "kotlin.collections.Map is not in the class path."
        }
    }

    private val makeMapFunctionName = CallableId(FqName("com.x12q.randomizer.lib.util"), Name.identifier("makeMap"))

    /**
     * Get a reference to kotlin.to function.
     */
    fun makeMapFunction(builder: IrBuilderWithScope): IrCall {
        val bmFunction = pluginContext.referenceFunctions(makeMapFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections.makeMap does not exist."
            }

        return builder.irCall(bmFunction)
    }
}
