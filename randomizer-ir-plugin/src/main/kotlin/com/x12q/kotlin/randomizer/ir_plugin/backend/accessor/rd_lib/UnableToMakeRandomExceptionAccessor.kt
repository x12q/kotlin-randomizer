package com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.withValueArgs
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.lib.UnableToMakeRandomException
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irNull
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject

class UnableToMakeRandomExceptionAccessor @Inject constructor(
    private val pluginContext: IrPluginContext,
) : ClassAccessor() {

    private val name = ClassId.topLevel(FqName(UnableToMakeRandomException::class.qualifiedName.crashOnNull {
        "Class UnableToMakeRandomException does not exist in the class path"
    }))

    override val clzz: IrClassSymbol by lazy { pluginContext.referenceClass(name)!! }

    val primaryConstructor by lazy {
        clzz.owner.primaryConstructor.crashOnNull {
            "UnableToMakeRandomException must have a primary constructor. This is a bug by the developer."
        }
    }

    fun callConstructor(builder: IrBuilderWithScope, msg: String?): IrConstructorCall {
        val msgIr: IrExpression = msg?.let{builder.irString(msg)} ?: builder.irNull()
        val rt = callConstructor(builder,msgIr )
        return rt
    }

    fun callConstructor(builder: IrBuilderWithScope, msgIr: IrExpression): IrConstructorCall {
        val rt = with(builder) {
            irCallConstructor(
                callee = primaryConstructor.symbol,
                typeArguments = emptyList()
            ).withValueArgs(msgIr) as IrConstructorCall
        }
        return rt
    }
}
