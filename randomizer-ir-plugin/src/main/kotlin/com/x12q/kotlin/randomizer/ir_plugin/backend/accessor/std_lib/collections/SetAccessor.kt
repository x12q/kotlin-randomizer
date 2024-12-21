package com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.std_lib.collections

import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.ClassAccessor
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

class SetAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {

    private val classId = ClassId.topLevel(FqName(Set::class.qualifiedName!!))

    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(classId)) {
            "kotlin.collections.Set is not in the class path."
        }
    }

    private val listToSetFunctionName = CallableId(libraryPackageName, Name.identifier("listToSet"))

    private val listToSetFunctionSymbol by lazy {
        pluginContext.referenceFunctions(listToSetFunctionName).firstOrNull()
            .crashOnNull {
                "function $listToSetFunctionName does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.listToSet]
     */
    fun listToSet(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(listToSetFunctionSymbol)
    }

    private val makeHashSetFunctionCallId = CallableId(libraryPackageName, Name.identifier("makeHashSet"))

    private val makeHashSetFunctionSymbol by lazy {
        pluginContext.referenceFunctions(makeHashSetFunctionCallId).firstOrNull()
            .crashOnNull {
                "function $makeHashSetFunctionCallId does not exist."
            }
    }
    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeHashSet]
     */
    fun makeHashSet(builder: IrBuilderWithScope):IrCall{
        return builder.irCall(makeHashSetFunctionSymbol)
    }

    private val makeLinkedHashSetCallId = CallableId(libraryPackageName, Name.identifier("makeLinkedHashSet"))

    private val makeLinkedHashSetFunctionSymbol by lazy {
        pluginContext.referenceFunctions(makeLinkedHashSetCallId).firstOrNull()
            .crashOnNull {
                "function $makeLinkedHashSetCallId does not exist."
            }
    }
    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeLinkedHashSet]
     */
    fun makeLinkedHashSet(builder: IrBuilderWithScope):IrCall{
        return builder.irCall(makeLinkedHashSetFunctionSymbol)
    }
}
