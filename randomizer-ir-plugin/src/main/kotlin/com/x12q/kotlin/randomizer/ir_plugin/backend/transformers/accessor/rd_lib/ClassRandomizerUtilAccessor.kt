package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
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
    private val packageName = FqName("${BaseObjects.COM_X12Q_KOTLIN_RANDOMIZER}.lib.randomizer")
    val factoryRandomizerCallId =
        CallableId(packageName, Name.identifier("factoryRandomizer"))

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
        CallableId(packageName, Name.identifier("constantRandomizer"))

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
