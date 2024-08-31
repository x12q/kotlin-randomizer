package com.x12q.randomizer.ir_plugin.backend.transformers.accessor

import com.x12q.randomizer.ir_plugin.backend.utils.oneAgrFunction
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import javax.inject.Inject

class RandomizerContextBuilderAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
) : ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.RandomContextBuilder_Id)) {
            "RandomizerContextBuilder interface is not in the class path."
        }
    }

    fun addForTier2():IrSimpleFunctionSymbol{
        return clzz.oneAgrFunction("addForTier2")
    }

    fun addForTier2Call(builder: IrBuilderWithScope):IrCall{
        return builder.oneAgrFunctionCall("addForTier2")
    }

    fun randomConfig(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(
            requireNotNull(clzz.getPropertyGetter("randomConfig")) {
                "randomConfig property must exist in RandomContextBuilder. This is a bug by the developer."
            }
        )
    }

    fun buildFunction(builder: IrBuilderWithScope): IrCall {
        return builder.zeroAgrFunctionCall("build")
    }

    fun buildRandomConfigFunction(builder: IrBuilderWithScope): IrCall {
        return builder.zeroAgrFunctionCall("buildContext")
    }

    fun addFunction(builder: DeclarationIrBuilder): IrCall {
        return builder.oneAgrFunctionCall("add")
    }

    fun setRandomConfigAndGenerateStandardRandomizersFunction(builder: DeclarationIrBuilder): IrCall {
        return builder.oneAgrFunctionCall("setRandomConfigAndGenerateStandardRandomizers")
    }
}
