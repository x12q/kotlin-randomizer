package com.x12q.randomizer.ir_plugin.backend.transformers.accessor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.lib.RandomContext
import com.x12q.randomizer.lib.RandomContextBuilder
import com.x12q.randomizer.lib.RandomizerCollection
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

    private val randomFunctionCallId = CallableId(packageName = FqName("com.x12q.randomizer.lib"), callableName = Name.identifier("random"))

    fun randomFunction(builder: DeclarationIrBuilder):IrCall{
        val function = requireNotNull(pluginContext.referenceFunctions(randomFunctionCallId).firstOrNull()){
            "com.x12q.randomizer.lib.randomizer.random on ${RandomizerCollection::class.simpleName} does not exist. This is a bug by the developer."
        }
        return builder.irCall(function)
    }

}
