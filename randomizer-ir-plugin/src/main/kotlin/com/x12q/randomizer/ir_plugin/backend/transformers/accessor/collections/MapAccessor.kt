package com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections

import com.x12q.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject


class MapAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Map::class.qualifiedName!!)))) {
            "kotlin.collections.Map is not in the class path."
        }
    }
    private val buildMapFunctionName = CallableId(FqName("kotlin.collections"), Name.identifier("buildMap"))
    fun buildMapFunction(builder: IrBuilderWithScope): IrCall {
        val bmFunction = requireNotNull(
            pluginContext.referenceFunctions(buildMapFunctionName).firstOrNull { function ->
                val correctSize = function.owner.valueParameters.let {
                    val correctArgCount = it.size == 1
                    correctArgCount
                }
                correctSize
            }
        ) {
            "function kotlin.collections.buildMap does not exist."
        }
        return builder.irCall(bmFunction)
    }
}
