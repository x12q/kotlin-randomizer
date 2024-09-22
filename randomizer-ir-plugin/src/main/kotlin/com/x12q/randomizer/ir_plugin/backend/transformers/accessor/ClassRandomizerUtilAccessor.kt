package com.x12q.randomizer.ir_plugin.backend.transformers.accessor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.crashOnNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class ClassRandomizerUtilAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) {

    val factoryRandomizerCallId =
        CallableId(FqName("com.x12q.randomizer.lib.randomizer"), Name.identifier("factoryRandomizer"))

    val factoryClassRandomizerFunction by lazy {
        val function = pluginContext.referenceFunctions(factoryRandomizerCallId).firstOrNull()
            .crashOnNull {
                "$factoryRandomizerCallId does not exist in the class path. This is a bug by the developer."
            }
        function
    }

    fun factoryClassRandomizerFunctionCall(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(factoryClassRandomizerFunction)
    }

    val constantRandomizerCallId =
        CallableId(FqName("com.x12q.randomizer.lib.randomizer"), Name.identifier("constantRandomizer"))

    val constantClassRandomizerFunction by lazy {
        val function = pluginContext.referenceFunctions(constantRandomizerCallId).firstOrNull()
            .crashOnNull {
                "$constantRandomizerCallId does not exist in the class path. This is a bug by the developer."
            }
        function
    }

    fun constantClassRandomizerFunctionCall(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(constantClassRandomizerFunction)
    }
}
