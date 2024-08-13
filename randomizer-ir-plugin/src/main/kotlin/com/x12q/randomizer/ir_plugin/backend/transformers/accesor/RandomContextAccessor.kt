package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import javax.inject.Inject

class RandomContextAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
) :ClassAccessor(){
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.RandomContext_ClassId)) {
            "RandomConfig interface is not in the class path."
        }
    }

    fun getRandomizersMap(builder:DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("getRandomizers")
    }

    fun randomConfig(builder:DeclarationIrBuilder):IrCall{
        val propGetter = requireNotNull(clzz.getPropertyGetter("randomConfig")){
            "RandomContext must have randomConfig property. This is a bug by the developer."
        }
        return builder.irCall(propGetter)
    }
}
