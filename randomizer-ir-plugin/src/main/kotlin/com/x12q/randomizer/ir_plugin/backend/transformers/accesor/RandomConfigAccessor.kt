package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import kotlin.random.Random

class RandomConfigAccessor @AssistedInject constructor(
    @Assisted
    private val randomConfigClass: IrClassSymbol
) : ClassAccessor(randomConfigClass) {

    val randomProperty by lazy {
        requireNotNull(randomConfigClass.getPropertyGetter("random")){
            "impossible, ${BaseObjects.randomConfigClassId.shortClassName} must provide a ${BaseObjects.randomClassId} instance"
        }
    }
    /**
     * Construct an [IrCall] to access [RandomConfig.random]
     */
    fun random(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(randomProperty)
    }

    /**
     * Construct an [IrCall] to access [RandomConfig.nextByte]
     */
    fun nextByte(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextByte")
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
     * Construct an [IrCall] to access [RandomConfig.nextStringUUID]
     */
    fun nextStringUUID(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextStringUUID")
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

    @AssistedFactory
    interface Factory {
        fun create(randomConfigClass: IrClassSymbol):RandomConfigAccessor
    }

}

