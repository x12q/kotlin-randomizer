package com.x12q.randomizer.ir_plugin.backend.transformers.accessor.std_lib.collections

import com.x12q.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.randomizer.ir_plugin.backend.utils.hasSignature
import com.x12q.randomizer.ir_plugin.util.crashOnNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class ArrayAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Array::class.qualifiedName!!)))) {
            "kotlin.collections.Array is not in the class path."
        }
    }

    fun isArray(irClass: IrClass):Boolean{
        return irClass.symbol == clzz
    }

    private val makeArrayFunctionSymbol by lazy {
        val makeListFunctionName = CallableId(FqName("com.x12q.randomizer.lib.util"), Name.identifier("makeArray"))
        pluginContext.referenceFunctions(makeListFunctionName).firstOrNull()
            .crashOnNull {
                "function com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections.makeArray does not exist."
            }
    }

    /**
     * Construct an ir call for [com.x12q.randomizer.lib.util.makeArray]
     */
    fun makeArray(builder: IrBuilderWithScope): IrCall {
        return builder.irCall(makeArrayFunctionSymbol)
    }
}