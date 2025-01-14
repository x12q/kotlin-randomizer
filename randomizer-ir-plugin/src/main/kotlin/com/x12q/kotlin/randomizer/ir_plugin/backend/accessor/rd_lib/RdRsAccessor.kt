package com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.backend.utils.withTypeArgs
import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.lib.NoRandomizerErr
import com.x12q.kotlin.randomizer.lib.RandomContext
import com.x12q.kotlin.randomizer.lib.rs.Ok
import com.x12q.kotlin.randomizer.lib.rs.RdRs
import com.x12q.kotlin.randomizer.lib.util.developerErrorMsg
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class RdRsAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
) :  ClassAccessor(){

    private val packageName = FqName("${BaseObjects.RANDOMIZER_LIB_ROOT_PACKAGE}.lib.rs")

    override val clzz: IrClassSymbol by lazy {
        val classId = ClassId.topLevel(FqName(RdRs::class.qualifiedName.crashOnNull {
            "RdRs class does not exist in the class path."
        }))
        pluginContext.referenceClass(classId)
            .crashOnNull { "RdRs class is not in the class path." }
    }

    val noRandomizerErrIrType  by lazy {
        val classId = ClassId.topLevel(FqName(NoRandomizerErr::class.qualifiedName.crashOnNull {
            "NoRandomizerErr class does not exist in the class path."
        }))
        pluginContext.referenceClass(classId)
            .crashOnNull { "NoRandomizerErr class is not in the class path." }
            .defaultType
    }

    private val okClzz: IrClassSymbol by lazy {
        val name = Ok::class.qualifiedName.crashOnNull {
            "RdRs.Ok class does not exist in the class path."
        }
        val classId = ClassId.topLevel(FqName(name))
        pluginContext.referenceClass(classId)
            .crashOnNull { "RdRs.Ok class is not in the class path." }
    }


    val isOkFunctionSymbol by lazy {
        val randomFunctionCallId = CallableId(packageName = packageName, callableName = Name.identifier("isOk"))
        pluginContext.referenceFunctions(randomFunctionCallId).firstOrNull()
            .crashOnNull {
                developerErrorMsg("$randomFunctionCallId on ${RandomContext::class.simpleName} does not exist.")
            }
    }

    fun isOkFunction(builder: DeclarationIrBuilder, vType: IrType, eType: IrType): IrCall{
        return builder
            .irCall(isOkFunctionSymbol)
            .withTypeArgs(vType, eType)
    }

    /**
     * Access `value` property of [RdRs.Ok]
     */
    fun value(builder: DeclarationIrBuilder):IrCall{
        val propGetter = okClzz.getPropertyGetter("value")
            .crashOnNull {
                developerErrorMsg("RdRs.Ok must have 'value' property.")
            }
        return builder.irCall(propGetter)
    }
}
