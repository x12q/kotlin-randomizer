package com.x12q.randomizer.ir_plugin.backend.transformers.accessor.rd_lib

import com.x12q.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.randomizer.ir_plugin.util.crashOnNull
import com.x12q.randomizer.lib.UnableToMakeRandomException
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class UnableToMakeRandomExceptionAccessor @Inject constructor(
    private val pluginContext: IrPluginContext,
) : ClassAccessor() {

    val name = ClassId.topLevel(FqName(UnableToMakeRandomException::class.qualifiedName.crashOnNull {
        "Class UnableToMakeRandomException does not exist in the class path"
    }))

    override val clzz: IrClassSymbol by lazy { pluginContext.referenceClass(name)!! }

    fun primaryConstructor(): IrConstructor {
        return clzz.owner.primaryConstructor.crashOnNull {
            "UnableToMakeRandomException must have a primary constructor. This is a bug by the developer."
        }
    }
}
