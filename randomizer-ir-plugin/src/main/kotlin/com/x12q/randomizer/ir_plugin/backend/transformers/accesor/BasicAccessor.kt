package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.types.isCollection
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.inject.Inject
import kotlin.random.Random

class BasicAccessor @Inject constructor(
    pluginContext: IrPluginContext
) {
    /**
     * Name for the family of random functions in the standard library that can be call on collections, arrays, etc
     */
    private val kotlinCollectionRandomFunctionName = CallableId(FqName("kotlin.collections"), Name.identifier("random"))

    /**
     * kotlin.collection.random on Collection<T>, that accept a [Random] argument
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    val randomFunctionOnCollectionOneArg:IrFunctionSymbol by lazy {
        val allRandoms =  pluginContext.referenceFunctions(kotlinCollectionRandomFunctionName)
        requireNotNull(allRandoms.firstOrNull { functionSymbol->
            val irFunction = functionSymbol.owner
            if(irFunction.valueParameters.size==1){
                val firstParam = irFunction.valueParameters.first()
                if(firstParam.type.classOrNull == kotlinRandomClass && irFunction.extensionReceiverParameter?.type?.isCollection() == true){
                    true
                }else{
                    false
                }
            }else{
                false
            }
        }){
            "random function from std library does not exist"
        }

    }

    /**
     * kotlin.collection.random on Collection<T>, that accept a [Random] argument
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    val randomFunctionOnArrayOneArg:IrFunctionSymbol by lazy {
        val allRandoms =  pluginContext.referenceFunctions(kotlinCollectionRandomFunctionName)
        requireNotNull(allRandoms.firstOrNull { functionSymbol->
            val irFunction = functionSymbol.owner
            if(irFunction.valueParameters.size==1){
                val firstParam = irFunction.valueParameters.first()
                if(firstParam.type.classOrNull == kotlinRandomClass && irFunction.extensionReceiverParameter?.type?.isArray() == true){
                    true
                }else{
                    false
                }
            }else{
                false
            }
        }){
            "random function from std library does not exist"
        }

    }

    val function0Class by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Function0::class.qualifiedName!!)))) {
            "kotlin.Function0 class is not in the class path."
        }
    }

    val kotlinRandomClass by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.randomClassId)) {
            "kotlin.random.Random class is not in the class path."
        }
    }
    val randomConfigClass by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.randomConfigClassId)) {
            "RandomConfig interface is not in the class path."
        }
    }
    val defaultRandomConfigClass by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.defaultRandomConfigClassId)) {
            "impossible, DefaultRandomConfig class must exist in the class path"
        }
    }

    val defaultRandomConfigCompanionObject by lazy {
        requireNotNull(defaultRandomConfigClass.owner.companionObject()) {
            "impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must exist"
        }
    }

    val getDefaultRandomConfigInstance by lazy {
        if (defaultRandomConfigCompanionObject.isObject) {
            requireNotNull(defaultRandomConfigCompanionObject.getPropertyGetter("default")) {
                "Impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must contain a \"default\" variable"
            }
        } else {
            throw IllegalArgumentException("Impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must be an object")
        }
    }
}
