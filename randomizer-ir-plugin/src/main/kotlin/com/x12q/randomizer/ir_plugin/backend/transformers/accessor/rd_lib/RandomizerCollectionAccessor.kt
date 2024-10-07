package com.x12q.randomizer.ir_plugin.backend.transformers.accessor.rd_lib

import com.x12q.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.crashOnNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import javax.inject.Inject

class RandomizerCollectionAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
): ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        pluginContext.referenceClass(BaseObjects.RandomizerCollection_Id).crashOnNull {
            "ClassRandomizerCollection class is not in the class path."
        }
    }

}
