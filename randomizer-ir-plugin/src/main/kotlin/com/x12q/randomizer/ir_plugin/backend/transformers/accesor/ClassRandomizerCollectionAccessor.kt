package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.lib.randomizer.ClassRandomizerCollection
import com.x12q.randomizer.lib.randomizer.ClassRandomizerCollectionImp
import com.x12q.randomizer.lib.randomizer.random
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class ClassRandomizerCollectionAccessor @Inject constructor(
    private val basicAccessor: BasicAccessor
):ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        basicAccessor.ClassRandomizerCollection_Class
    }

    private val randomFunctionCallId = CallableId(packageName = FqName("com.x12q.randomizer.lib.randomizer"), callableName = Name.identifier("random"))

    fun randomFunction(builder: DeclarationIrBuilder):IrCall{
        val function = requireNotNull(basicAccessor.pluginContext.referenceFunctions(randomFunctionCallId).firstOrNull()){
            "com.x12q.randomizer.lib.randomizer.random on ${ClassRandomizerCollection::class.simpleName} does not exist. This is a bug by the developer."
        }
        return builder.irCall(function)
    }
}
