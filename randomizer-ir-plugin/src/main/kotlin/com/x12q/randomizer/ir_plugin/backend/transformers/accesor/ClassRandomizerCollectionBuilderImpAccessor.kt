package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.primaryConstructor
import javax.inject.Inject

class ClassRandomizerCollectionBuilderImpAccessor @Inject constructor(
    private val basicAccessor: BasicAccessor
) : ClassAccessor() {
    override val clzz: IrClassSymbol = basicAccessor.ClassRandomizerCollectionBuilderImp_Class

    fun constructorFunction(builder: DeclarationIrBuilder): IrConstructorCall {
        val constructorSymbol =
            requireNotNull(basicAccessor.ClassRandomizerCollectionBuilderImp_Class.owner.primaryConstructor?.symbol) {
                "ClassRandomizerCollectionBuilderImp must have a no-arg primary constructor. This is a bug by the developer."
            }

        val rt = builder.irCallConstructor(constructorSymbol, emptyList())
        return rt
    }
}
