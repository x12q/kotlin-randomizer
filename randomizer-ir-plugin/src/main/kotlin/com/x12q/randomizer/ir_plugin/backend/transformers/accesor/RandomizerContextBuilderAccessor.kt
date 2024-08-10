package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import javax.inject.Inject

class RandomizerContextBuilderAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
):ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.RandomContextBuilder_Id)) {
            "RandomizerContextBuilder interface is not in the class path."
        }
    }

    fun buildFunction(builder: IrBuilderWithScope): IrCall {
        return builder.zeroAgrFunctionCall("build")
    }

    fun buildRandomConfigFunction(builder: IrBuilderWithScope): IrCall {
        return builder.zeroAgrFunctionCall("buildContext")
    }

    fun addFunction(builder: DeclarationIrBuilder):IrCall{
        return builder.oneAgrFunctionCall("add")
    }

    fun setRandomConfigFunction(builder: DeclarationIrBuilder):IrCall{
        return builder.oneAgrFunctionCall("setRandomConfig")
    }
}
