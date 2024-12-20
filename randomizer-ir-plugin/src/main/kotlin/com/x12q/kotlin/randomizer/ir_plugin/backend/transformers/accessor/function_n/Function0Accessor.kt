package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.function_n

import com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class Function0Accessor @Inject constructor(
    private val pluginContext: IrPluginContext
) : ClassAccessor() {

    private val classId = ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Function0"))

    override val clzz: IrClassSymbol by lazy {
        pluginContext.referenceClass(classId)
            .crashOnNull {
                "kotlin.Function0 class is not in the class path."
            }
    }

    private val invokeFunction: IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("invoke")
    }

    fun invokeFunction(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(invokeFunction)
    }
}
