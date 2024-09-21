package com.x12q.randomizer.ir_plugin.backend.transformers.accessor.collections

import com.x12q.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.randomizer.ir_plugin.backend.utils.isClass
import com.x12q.randomizer.ir_plugin.backend.utils.isClassType2
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.isClass
import org.jetbrains.kotlin.ir.util.isVararg
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





    private val mapOfFunctionName = CallableId(FqName("kotlin.collections"), Name.identifier("mapOf"))
    /**
     * Get a reference of the mapOf(vararg pairs) function.
     */
    fun mapOf(builder: IrBuilderWithScope):IrCall{
        val bmFunction = requireNotNull(
            pluginContext.referenceFunctions(mapOfFunctionName).firstOrNull { function ->
                val correctSize = function.owner.valueParameters.let {
                    val correctArgCount = it.size == 1
                    correctArgCount
                }
                if(correctSize){
                    val firstParam = requireNotNull(function.owner.valueParameters.first()){
                        "This can't be null at this point"
                    }
                    val v = firstParam.isVararg
                    val t = firstParam.type.classOrNull?.owner?.isClass(Pair::class) == true
                    v && t
                }else{
                    false
                }
            }
        ) {
            "function kotlin.collections.mapOf does not exist."
        }
        return builder.irCall(bmFunction)
    }
    private val makePairFunctionName = CallableId(FqName("com.x12q.randomizer.lib.util"), Name.identifier("makePair"))

    /**
     * Get a reference to kotlin.to function.
     */
    fun makePairFunction(builder: IrBuilderWithScope):IrCall{
        val bmFunction = requireNotNull(
            pluginContext.referenceFunctions(makePairFunctionName).firstOrNull()
        ) {
            "function com.x12q.randomizer.lib.util.makePair does not exist."
        }
        return builder.irCall(bmFunction)
    }

    private val makeMapFunctionName = CallableId(FqName("com.x12q.randomizer.lib.util"), Name.identifier("makeMap"))

    /**
     * Get a reference to kotlin.to function.
     */
    fun makeMapFunction(builder: IrBuilderWithScope):IrCall{
        val bmFunction = requireNotNull(
            pluginContext.referenceFunctions(makeMapFunctionName).firstOrNull()
        ) {
            "function com.x12q.randomizer.lib.util.makeMap does not exist."
        }
        return builder.irCall(bmFunction)
    }
}
