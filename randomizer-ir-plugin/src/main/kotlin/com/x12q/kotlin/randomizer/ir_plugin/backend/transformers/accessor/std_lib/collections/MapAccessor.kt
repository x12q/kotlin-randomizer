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

    private val classId = ClassId.topLevel(FqName(Map::class.qualifiedName!!))

    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(classId)) {
            "kotlin.collections.Map is not in the class path."
        }
    }

    private val makeMapFunctionName = CallableId(libraryPackageName, Name.identifier("makeMap"))

    private val makeMapFunctionSymbol by lazy {

        pluginContext.referenceFunctions(makeMapFunctionName).firstOrNull()
            .crashOnNull {
                "function $makeMapFunctionName does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeMap]
     */
    fun makeMapFunction(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeMapFunctionSymbol)
    }

    private val makeHashMapFunctionName = CallableId(libraryPackageName, Name.identifier("makeHashMap"))

    private val makeHashMapFunctionSymbol by lazy {
        pluginContext.referenceFunctions(makeHashMapFunctionName).firstOrNull()
            .crashOnNull {
                "function $makeHashMapFunctionName does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeHashMap]
     */
    fun makeHashMap(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeHashMapFunctionSymbol)
    }

    private val makeLinkedHashMapFunctionName = CallableId(libraryPackageName, Name.identifier("makeLinkedHashMap"))

    private val makeLinkedHashMapFunctionSymbol by lazy {
        pluginContext.referenceFunctions(makeLinkedHashMapFunctionName).firstOrNull()
            .crashOnNull {
                "function $makeLinkedHashMapFunctionName does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeLinkedHashMap]
     */
    fun makeLinkedHashMap(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeLinkedHashMapFunctionSymbol)
    }

}
