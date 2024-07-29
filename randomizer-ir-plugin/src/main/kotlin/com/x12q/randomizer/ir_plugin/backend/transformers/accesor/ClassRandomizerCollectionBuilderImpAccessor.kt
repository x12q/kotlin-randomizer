package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.lib.randomizer.ClassRandomizerCollectionBuilderImp
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class ClassRandomizerCollectionBuilderImpAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
) : ClassAccessor() {

    private val classId = ClassId.topLevel(
        FqName(
            requireNotNull(ClassRandomizerCollectionBuilderImp::class.qualifiedName){
                "ClassRandomizerCollectionBuilder interface does not exist in the class path"
            }
        )
    )

    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(classId)) {
            "RandomizerCollectionBuilderImp class is not in the class path."
        }
    }

    fun constructorFunction(builder: IrBuilderWithScope): IrConstructorCall {
        val constructorSymbol =
            requireNotNull(clzz.owner.primaryConstructor?.symbol) {
                "ClassRandomizerCollectionBuilderImp must have a no-arg primary constructor. This is a bug by the developer."
            }

        val rt = builder.irCallConstructor(constructorSymbol, emptyList())
        return rt
    }
}
