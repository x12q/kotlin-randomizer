package com.x12q.randomizer.ir_plugin.backend.transformers.accesor


import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import javax.inject.Inject
import kotlin.random.Random

/**
 * Provide convenient access to kotlin.random.Random member function symbols
 */
class RandomAccessor @Inject constructor(
    private val basicAccessor:BasicAccessor,
):ClassAccessor() {

    override val clzz: IrClassSymbol by lazy { basicAccessor.kotlinRandom_Class }

    private val nextIntFunction:IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextInt")
    }

    /**
     * Construct an [IrCall] to access [Random.nextInt]
     */
    fun nextInt(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextIntFunction)
    }

    private val nextLongFunction:IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextLong")
    }

    /**
     * Construct an [IrCall] to access [Random.nextLong]
     */
    fun nextLong(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextLongFunction)
    }

    val nextDoubleFunction:IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextDouble")
    }
    /**
     * Construct an [IrCall] to access [Random.nextDouble]
     */
    fun nextDouble(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextDoubleFunction)
    }

    private val nextDoubleUntil:IrSimpleFunctionSymbol by lazy {
        oneAgrFunction("nextDouble")
    }

    private val nextDoubleBetween:IrSimpleFunctionSymbol by lazy {
        oneAgrFunction("nextDouble")
    }

    private val nextFloatFunction:IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextFloat")
    }

    /**
     * Construct an [IrCall] to access [Random.nextFloat]
     */
    fun nextFloat(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextFloatFunction)
    }

    private val nextBooleanFunction:IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextBoolean")
    }

    /**
     * Construct an [IrCall] to access [Random.nextBoolean]
     */
    fun nextBoolean(builder: DeclarationIrBuilder):IrCall{
        return builder.irCall(nextBooleanFunction)
    }
}
