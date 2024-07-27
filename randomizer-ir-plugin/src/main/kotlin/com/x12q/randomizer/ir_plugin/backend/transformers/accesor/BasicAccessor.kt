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

/**
 * Contain declaration to access basic classes that are relevant to the randomizer plugin. Including:
 * - class from standard kotlin library
 * - class from support libraries of randomizer plugin
 */
class BasicAccessor @Inject constructor(
    val pluginContext: IrPluginContext
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
                if(firstParam.type.classOrNull == kotlinRandom_Class && irFunction.extensionReceiverParameter?.type?.isCollection() == true){
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
                if(firstParam.type.classOrNull == kotlinRandom_Class && irFunction.extensionReceiverParameter?.type?.isArray() == true){
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

    val ClassRandomizerCollection_Class  by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.ClassRandomizerCollection_ClassId)) {
            "ClassRandomizerCollection class is not in the class path."
        }
    }

    val ClassRandomizerCollectionBuilder_Interface by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.ClassRandomizerCollectionBuilder_ClassId)) {
            "RandomizerCollectionBuilder interface is not in the class path."
        }
    }

    val ClassRandomizerCollectionBuilderImp_Class by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.ClassRandomizerCollectionBuilderImp_ClassId)) {
            "RandomizerCollectionBuilderImp class is not in the class path."
        }
    }

    val Function0_Class by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Function0::class.qualifiedName!!)))) {
            "kotlin.Function0 class is not in the class path."
        }
    }

    val Function1_Class by lazy {
        requireNotNull(pluginContext.referenceClass(ClassId.topLevel(FqName(Function1::class.qualifiedName!!)))) {
            "kotlin.Function1 class is not in the class path."
        }
    }

    val kotlinRandom_Class by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.Random_ClassId)) {
            "kotlin.random.Random class is not in the class path."
        }
    }
    val RandomConfig_Class by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.RandomConfig_ClassId)) {
            "RandomConfig interface is not in the class path."
        }
    }
    val DefaultRandomConfig_Class by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.DefaultRandomConfig_ClassId)) {
            "impossible, DefaultRandomConfig class must exist in the class path"
        }
    }

    val defaultRandomConfigCompanionObject by lazy {
        requireNotNull(DefaultRandomConfig_Class.owner.companionObject()) {
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
