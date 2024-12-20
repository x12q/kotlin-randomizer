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


class MapAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Map::class.qualifiedName!!)))) {
            "kotlin.collections.Map is not in the class path."
        }
    }

    private val makeMapFunctionSymbol by lazy {
        val makeMapFunctionName = CallableId(FqName("com.x12q.kotlin.randomizer.lib.util"), Name.identifier("makeMap"))
        pluginContext.referenceFunctions(makeMapFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections.makeMap does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeMap]
     */
    fun makeMapFunction(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeMapFunctionSymbol)
    }

    private val makeHashMapFunctionSymbol by lazy {
        val makeMapFunctionName = CallableId(FqName("com.x12q.kotlin.randomizer.lib.util"), Name.identifier("makeHashMap"))
        pluginContext.referenceFunctions(makeMapFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections.makeHashMap does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeHashMap]
     */
    fun makeHashMap(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeHashMapFunctionSymbol)
    }

    private val makeLinkedHashMapFunctionSymbol by lazy {
        val makeMapFunctionName = CallableId(FqName("com.x12q.kotlin.randomizer.lib.util"), Name.identifier("makeLinkedHashMap"))
        pluginContext.referenceFunctions(makeMapFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections.makeLinkedHashMap does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeLinkedHashMap]
     */
    fun makeLinkedHashMap(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeLinkedHashMapFunctionSymbol)
    }

}
