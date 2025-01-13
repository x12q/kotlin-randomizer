package com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.base.BaseObjects
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.lib.RandomContext
import com.x12q.kotlin.randomizer.lib.util.developerErrorMsg
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject

class RandomContextAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
) : ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        val classId = ClassId.topLevel(FqName(requireNotNull(RandomContext::class.qualifiedName) {
            "RandomContext interface does not exist in the class path."
        }))
        pluginContext.referenceClass(classId)
            .crashOnNull {
                "RandomContext interface is not in the class path."
            }
    }

    private val packageName = FqName("${BaseObjects.COM_X12Q_KOTLIN_RANDOMIZER}.lib")

    fun randomConfig(builder: DeclarationIrBuilder): IrCall {
        val propGetter = clzz.getPropertyGetter("randomConfig")
            .crashOnNull {
                "RandomContext must have randomConfig property. This is a bug by the developer."
            }
        return builder.irCall(propGetter)
    }

    private val randomFunctionSymbol by lazy {
        val randomFunctionCallId = CallableId(packageName = packageName, callableName = Name.identifier("random"))
        pluginContext.referenceFunctions(randomFunctionCallId).firstOrNull()
            .crashOnNull {
                developerErrorMsg("$randomFunctionCallId on ${RandomContext::class.simpleName} does not exist.")
            }
    }

    fun randomFunction(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(randomFunctionSymbol)
    }

    private val randomRsFunctionSymbol by lazy {
        val callId = CallableId(packageName = packageName, callableName = Name.identifier("randomRs"))
        pluginContext.referenceFunctions(callId).firstOrNull()
            .crashOnNull {
                developerErrorMsg("$callId on ${RandomContext::class.simpleName} does not exist.")
            }
    }

    fun randomRsFunction(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(randomRsFunctionSymbol)
    }

    private val randomListFunctionSymbol by lazy {
        val callId = CallableId(packageName = packageName, callableName = Name.identifier("randomList"))
        pluginContext.referenceFunctions(callId).firstOrNull()
            .crashOnNull {
                developerErrorMsg("$callId on ${RandomContext::class.simpleName} does not exist.")
            }
    }

    fun randomList(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(randomListFunctionSymbol)
    }


    private val randomMapFunctionSymbol by lazy {
        val callId = CallableId(packageName = packageName, callableName = Name.identifier("randomMap"))
        pluginContext.referenceFunctions(callId).firstOrNull()
            .crashOnNull {
                developerErrorMsg("$callId on ${RandomContext::class.simpleName} does not exist.")
            }
    }

    fun randomMap(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(randomMapFunctionSymbol)
    }
}
