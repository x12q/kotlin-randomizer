package com.x12q.randomizer.ir_plugin.backend.transformers.accessor


import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.crashOnNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.types.isCollection
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject
import kotlin.random.Random

/**
 * Provide convenient access to kotlin.random.Random member function symbols
 */
class RandomAccessor @Inject constructor(
    private val pluginContext: IrPluginContext,
) : ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        pluginContext.referenceClass(BaseObjects.Random_ClassId)
            .crashOnNull {
                "kotlin.random.Random class is not in the class path."
            }
    }


    /**
     * Name for the family of random functions in the standard library that can be call on collections, arrays, etc
     */
    private val kotlinCollectionRandomFunctionName = CallableId(FqName("kotlin.collections"), Name.identifier("random"))

    /**
     * kotlin.collection.random on Collection<T>, that accept a [Random] argument
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    val randomFunctionOnCollectionOneArg: IrFunctionSymbol by lazy {
        val allRandoms = pluginContext.referenceFunctions(kotlinCollectionRandomFunctionName)
        requireNotNull(allRandoms.firstOrNull { functionSymbol ->
            val irFunction = functionSymbol.owner
            if (irFunction.valueParameters.size == 1) {
                val firstParam = irFunction.valueParameters.first()
                if (firstParam.type.classOrNull == clzz && irFunction.extensionReceiverParameter?.type?.isCollection() == true) {
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }) {
            "random function from std library does not exist"
        }
    }

    /**
     * kotlin.collection.random on Collection<T>, that accept a [Random] argument
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    val randomFunctionOnArrayOneArg: IrFunctionSymbol by lazy {
        val allRandoms = pluginContext.referenceFunctions(kotlinCollectionRandomFunctionName)
        requireNotNull(allRandoms.firstOrNull { functionSymbol ->
            val irFunction = functionSymbol.owner
            if (irFunction.valueParameters.size == 1) {
                val firstParam = irFunction.valueParameters.first()
                if (firstParam.type.classOrNull == clzz && irFunction.extensionReceiverParameter?.type?.isArray() == true) {
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }) {
            "random function from std library does not exist"
        }
    }


    private val nextIntFunction: IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextInt")
    }

    /**
     * Construct an [IrCall] to access [Random.nextInt]
     */
    fun nextInt(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(nextIntFunction)
    }

    private val nextLongFunction: IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextLong")
    }

    /**
     * Construct an [IrCall] to access [Random.nextLong]
     */
    fun nextLong(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(nextLongFunction)
    }

    val nextDoubleFunction: IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextDouble")
    }

    /**
     * Construct an [IrCall] to access [Random.nextDouble]
     */
    fun nextDouble(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(nextDoubleFunction)
    }

    private val nextDoubleUntil: IrSimpleFunctionSymbol by lazy {
        oneAgrFunction("nextDouble")
    }

    private val nextDoubleBetween: IrSimpleFunctionSymbol by lazy {
        oneAgrFunction("nextDouble")
    }

    private val nextFloatFunction: IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextFloat")
    }

    /**
     * Construct an [IrCall] to access [Random.nextFloat]
     */
    fun nextFloat(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(nextFloatFunction)
    }

    private val nextBooleanFunction: IrSimpleFunctionSymbol by lazy {
        zeroAgrFunction("nextBoolean")
    }

    /**
     * Construct an [IrCall] to access [Random.nextBoolean]
     */
    fun nextBoolean(builder: DeclarationIrBuilder): IrCall {
        return builder.irCall(nextBooleanFunction)
    }
}
