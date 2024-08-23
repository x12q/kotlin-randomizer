package com.x12q.randomizer.ir_plugin.backend.transformers.accessor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.lib.UnableToGenerateRandomException
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class UnableToGenerateRandomExceptionAccessor @Inject constructor(
    private val pluginContext: IrPluginContext,
) : ClassAccessor() {

    val name = ClassId.topLevel(FqName(requireNotNull(UnableToGenerateRandomException::class.qualifiedName){
        "Class UnableToGenerateRandomException does not exist in the class path"
    }))

    override val clzz: IrClassSymbol by lazy { pluginContext.referenceClass(name)!! }

    fun primaryConstructor(): IrConstructor {
        return requireNotNull(clzz.owner.primaryConstructor){
            "UnableToGenerateRandomException must have a primary constructor. This is a bug by the developer."
        }
    }
}
