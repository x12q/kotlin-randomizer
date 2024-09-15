package com.x12q.randomizer.ir_plugin.backend.transformers.accessor

import com.x12q.randomizer.lib.RandomConfig
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import javax.inject.Inject

class RandomConfigAccessor @Inject constructor(
    private val pluginContext: IrPluginContext
) : ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.RandomConfig_ClassId)) {
            "RandomConfig interface is not in the class path."
        }
    }

    private val randomProperty by lazy {
        requireNotNull(clzz.getPropertyGetter("random")){
            "impossible, ${BaseObjects.RandomConfig_ClassId.shortClassName} must provide a ${BaseObjects.Random_ClassId} instance"
        }
    }

    fun randomCollectionSize(builder: IrBuilderWithScope):IrCall{
        return builder.zeroAgrFunctionCall("randomCollectionSize")
    }

    fun nextAny(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextAny")
    }

    /**
     * Construct an [IrCall] to access [RandomConfig.random]
     */
    fun random(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(randomProperty)
    }

    fun nextBoolean(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextBoolean")
    }

    fun nextFloat(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextFloat")
    }

    fun nextDouble(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextDouble")
    }

    /**
     * Construct an [IrCall] to access [RandomConfig.nextByte]
     */
    fun nextByte(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextByte")
    }

    fun nextUByte(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUByte")
    }


    /**
     * Construct an [IrCall] to access [RandomConfig.nextChar]
     */
    fun nextChar(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextChar")
    }

    /**
     * Construct an [IrCall] to access [RandomConfig.nextShort]
     */
    fun nextShort(builder:DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextShort")
    }

    /**
     * Construct an [IrCall] to access [RandomConfig.nextUShort]
     */
    fun nextUShort(builder:DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUShort")
    }


    fun nextULong(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextULong")
    }


    /**
     * Construct an [IrCall] to access [RandomConfig.nextString]
     */
    fun nextStringUUID(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextString")
    }
    /**
     * Construct an [IrCall] to access [RandomConfig.nextUnit]
     */
    fun nextUnit(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUnit")
    }

    fun nextNumber(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextNumber")
    }

    fun nextInt(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextInt")
    }

    fun nextLong(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextLong")
    }


    fun nextUInt(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUInt")
    }

}

