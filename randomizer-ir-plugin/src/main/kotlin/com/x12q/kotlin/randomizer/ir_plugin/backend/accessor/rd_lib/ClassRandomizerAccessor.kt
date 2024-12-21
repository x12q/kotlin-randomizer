package com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class ClassRandomizerAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {
    val name = ClassId.topLevel(
        FqName(ClassRandomizer::class.qualifiedName
            .crashOnNull {
                "Class ClassRandomizer does not exist in the class path"
            }
        )
    )

    override val clzz: IrClassSymbol by lazy { pluginContext.referenceClass(name)!! }
}

