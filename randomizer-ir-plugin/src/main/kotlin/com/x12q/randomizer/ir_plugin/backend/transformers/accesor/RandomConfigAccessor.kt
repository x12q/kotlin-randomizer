package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import javax.inject.Inject

class RandomConfigAccessor @Inject constructor(
    private val baseClassAccessor: BasicAccessor,
) : ClassAccessor() {
    override val clzz: IrClassSymbol by lazy {baseClassAccessor.randomConfigClass}

    private val randomProperty by lazy {
        requireNotNull(clzz.getPropertyGetter("random")){
            "impossible, ${BaseObjects.randomConfigClassId.shortClassName} must provide a ${BaseObjects.randomClassId} instance"
        }
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

    fun nextUByteOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUByteOrNull")
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

    /**
     * Construct an [IrCall] to access [RandomConfig.nextUShortOrNull]
     */
    fun nextUShortOrNull(builder:DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUShortOrNull")
    }

    fun nextULong(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextULong")
    }

    fun nextULongOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextULongOrNull")
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

    fun nextInt(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextInt")
    }

    fun nextLong(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextLong")
    }

    fun nextIntOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextIntOrNull")
    }

    fun nextUInt(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUInt")
    }

    fun nextUIntOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUIntOrNull")
    }

    fun nextBoolOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextBoolOrNull")
    }

    fun nextFloatOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextFloatOrNull")
    }

    fun nextLongOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextLongOrNull")
    }

    fun nextDoubleOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextDoubleOrNull")
    }

    fun nextCharOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextCharOrNull")
    }

    fun nextByteOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextByteOrNull")
    }

    fun nextShortOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextShortOrNull")
    }

    fun nextStringUUIDOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextStringUUIDOrNull")
    }

    fun nextUnitOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextUnitOrNull")
    }

    fun nextNumberOrNull(builder: DeclarationIrBuilder):IrCall{
        return builder.zeroAgrFunctionCall("nextNumberOrNull")
    }
}

