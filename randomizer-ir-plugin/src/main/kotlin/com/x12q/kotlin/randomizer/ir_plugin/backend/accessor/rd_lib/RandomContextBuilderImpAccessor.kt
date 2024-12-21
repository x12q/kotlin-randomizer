package com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderImp
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class RandomContextBuilderImpAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
) : ClassAccessor() {

    private val classId = ClassId.topLevel(
        FqName(
            RandomContextBuilderImp::class.qualifiedName
                .crashOnNull { "Class RandomContextBuilder interface does not exist in the class path" }
        )
    )

    override val clzz: IrClassSymbol by lazy {
        pluginContext.referenceClass(classId).crashOnNull {
            "RandomContextBuilderImp class is not in the class path."
        }
    }

    fun constructorFunction(builder: IrBuilderWithScope): IrConstructorCall {
        val constructorSymbol = clzz.owner.primaryConstructor?.symbol
            .crashOnNull {
                "RandomBuilderImp must have a no-arg primary constructor. This is a bug by the developer."
            }

        val rt = builder.irCallConstructor(constructorSymbol, emptyList())
        return rt
    }
}
