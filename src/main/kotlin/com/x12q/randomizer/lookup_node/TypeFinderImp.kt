package com.x12q.randomizer.lookup_node

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.primaryConstructor

class TypeFinderImp(
    val top: RDClassData
) {

    val topTypeSet: List<KTypeParameter> = top.kClass.typeParameters

    val topTypeSig: List<ShallowTypeSignature> = topTypeSet.map {
        ShallowTypeSignature(it.name, top.kClass.qualifiedName)
    }
    val topTypeMap: Map<ShallowTypeSignature, KTypeParameter> = topTypeSig.zip(topTypeSet).toMap()

    val typeSigMap: MutableMap<ShallowTypeSignature, ShallowTypeSignature> = mutableMapOf<ShallowTypeSignature, ShallowTypeSignature>().also { m->
        topTypeSig.forEach {
            m[it] = it
        }
    }

    /**
     * enclosing class = class that contain the param
     */
    private fun getSuppliedTypes(enclosingClass: KClass<*>, param: KParameter): List<ShallowTypeSignature> {
        return param.type.arguments.mapNotNull {
            (it.type?.classifier as? KTypeParameter)?.let {
                ShallowTypeSignature(
                    it.name, enclosingClass.qualifiedName
                )
            }
        }
    }

    /**
     *
     */
    private fun getInnerTypes(kParam: KParameter): List<ShallowTypeSignature>? {
        val classifier = kParam.type.classifier
        val innerTypes = when (classifier) {
            is KClass<*> -> {
                val clzzName = classifier.qualifiedName
                val rt = classifier.primaryConstructor?.typeParameters?.map {
                    it.name
                }?.map {
                    ShallowTypeSignature(it, clzzName)
                }
                rt
            }
            else -> null
        }
        return innerTypes
    }


    fun getTypeMap(enclosingClass: KClass<*>, kParam: KParameter): Map<ShallowTypeSignature, ShallowTypeSignature>? {
        val innerTypes = getInnerTypes(kParam)
        if (innerTypes != null) {
            val suppliedType = getSuppliedTypes(enclosingClass,kParam)
            return innerTypes.zip(suppliedType).toMap()
        } else {
            return null
        }
    }

    /**
     * enclosing class = class that contain that paremeter
     */
    fun append(enclosingClass: KClass<*>, parameter: KParameter): TypeFinderImp {
        val paramTypeMap = getTypeMap(enclosingClass,parameter)
        if (paramTypeMap != null) {
            for ((innerType, suppliedType) in paramTypeMap) {
                if (suppliedType in topTypeSig) {
                    typeSigMap[innerType] = suppliedType
                } else {
                    val topTypeEquivalentToSuppliedType = typeSigMap[suppliedType]
                    if (topTypeEquivalentToSuppliedType != null) {
                        typeSigMap[innerType] = topTypeEquivalentToSuppliedType
                    } else {
                        // inner type is mapped to something that can not be traced back to any top type
                    }
                }
            }
        }
        return this
    }

    fun getDataFor(enclosingClass: KClass<*>, typeParam: KTypeParameter): RDClassData? {
        val typeSig = ShallowTypeSignature(
            typeParam.name,
            enclosingClass.qualifiedName,
        )
        val topTypeSig = typeSigMap[typeSig]?.let {
            topTypeMap[it]
        }
        val rt = topTypeSig?.let {
            top.getDataFor(it)
        }
        return rt
    }
}
