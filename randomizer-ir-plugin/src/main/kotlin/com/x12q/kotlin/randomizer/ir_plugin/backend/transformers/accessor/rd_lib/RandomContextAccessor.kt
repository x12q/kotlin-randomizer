package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.lib.RandomContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class RandomContextAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
) : ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        pluginContext.referenceClass(BaseObjects.RandomContext_ClassId)
            .crashOnNull {
                "RandomConfig interface is not in the class path."
            }
    }

    private val packageName = FqName("com.x12q.kotlin.randomizer.lib")
    fun randomConfig(builder: DeclarationIrBuilder): IrCall {
        val propGetter = clzz.getPropertyGetter("randomConfig")
            .crashOnNull {
                "RandomContext must have randomConfig property. This is a bug by the developer."
            }
        return builder.irCall(propGetter)
    }

    private val randomFunctionCallId =
        CallableId(packageName = packageName, callableName = Name.identifier("random"))

    private val randomFunctionSymbol by lazy {
        pluginContext.referenceFunctions(randomFunctionCallId).firstOrNull()
            .crashOnNull {
                "$randomFunctionCallId on ${RandomContext::class.simpleName} does not exist. This is a bug by the developer."
            }
    }

    fun randomFunction(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(randomFunctionSymbol)
    }


    private val randomListFunctionCallId =
        CallableId(packageName = packageName, callableName = Name.identifier("randomList"))
    private val randomListFunctionSymbol by lazy {
        pluginContext.referenceFunctions(randomListFunctionCallId).firstOrNull()
            .crashOnNull {
                "$randomListFunctionCallId on ${RandomContext::class.simpleName} does not exist. This is a bug by the developer."
            }
    }

    fun randomList(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(randomListFunctionSymbol)
    }

    private val randomMapFunctionCallId =
        CallableId(packageName = packageName, callableName = Name.identifier("randomMap"))
    private val randomMapFunctionSymbol by lazy {
        pluginContext.referenceFunctions(randomMapFunctionCallId).firstOrNull()
            .crashOnNull {
                "$randomMapFunctionCallId on ${RandomContext::class.simpleName} does not exist. This is a bug by the developer."
            }
    }

    fun randomMap(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(randomMapFunctionSymbol)
    }
}
