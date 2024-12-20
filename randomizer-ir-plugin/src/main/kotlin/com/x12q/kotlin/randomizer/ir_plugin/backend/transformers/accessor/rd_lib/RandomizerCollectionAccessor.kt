package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.lib.RandomizerCollection
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class RandomizerCollectionAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
): ClassAccessor() {


    private val classId = ClassId.topLevel(
        FqName(
            requireNotNull(RandomizerCollection::class.qualifiedName) {
                "ClassRandomizerCollection interface does not exist in the class path"
            }
        )
    )

    override val clzz: IrClassSymbol by lazy {
        pluginContext.referenceClass(classId).crashOnNull {
            "ClassRandomizerCollection class is not in the class path."
        }
    }

}
