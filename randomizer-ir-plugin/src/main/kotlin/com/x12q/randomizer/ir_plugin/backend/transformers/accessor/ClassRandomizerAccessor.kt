package com.x12q.randomizer.ir_plugin.backend.transformers.accessor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.lib.ClassRandomizer
import com.x12q.randomizer.lib.UnableToMakeRandomException
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class ClassRandomizerAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor(){
    val name = ClassId.topLevel(FqName(requireNotNull(ClassRandomizer::class.qualifiedName){
        "Class ClassRandomizer does not exist in the class path"
    }))

    override val clzz: IrClassSymbol by lazy { pluginContext.referenceClass(name)!! }
}
