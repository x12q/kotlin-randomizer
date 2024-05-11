package com.x12q.randomizer.lookup_node

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.primaryConstructor

/**
 * This does not work with duplicate generic
 */
class TypeFinderImp(
    val top: RDClassData
) {

    val topTypeParameters: List<KTypeParameter> = top.kClass.typeParameters

    val topTypeSignatures: List<ShallowTypeSignature> = topTypeParameters.map {
        ShallowTypeSignature(it.name, top.kClass.qualifiedName)
    }
    val topTypeMap: Map<ShallowTypeSignature, KTypeParameter> = topTypeSignatures.zip(topTypeParameters).toMap()

    val typeMap: MutableMap<ShallowTypeSignature, ShallowTypeSignature> =
        mutableMapOf<ShallowTypeSignature, ShallowTypeSignature>().also { m ->
            // map each top type signature to itself.
            topTypeSignatures.forEach {
                m[it] = it
            }
        }

    /**
     * enclosing class = class that contain the param
     */
    fun getSuppliedTypes(enclosingClass: KClass<*>, param: KParameter): List<ShallowTypeSignature> {
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
    fun getInnerTypes(kParam: KParameter): List<ShallowTypeSignature>? {
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
            val suppliedType = getSuppliedTypes(enclosingClass, kParam)
            return innerTypes.zip(suppliedType).toMap()
        } else {
            return null
        }
    }

    /**
     * enclosing class = class that contain that paremeter
     */
    fun updateWith(enclosingClass: KClass<*>, parameter: KParameter): TypeFinderImp {
        val paramTypeMap: Map<ShallowTypeSignature, ShallowTypeSignature>? = getTypeMap(enclosingClass, parameter)
        if (paramTypeMap != null) {
            for ((innerType, suppliedType) in paramTypeMap) {
                if (suppliedType in topTypeSignatures) {
                    typeMap[innerType] = suppliedType
                } else {
                    val topTypeEquivalentToSuppliedType = typeMap[suppliedType]
                    if (topTypeEquivalentToSuppliedType != null) {
                        typeMap[innerType] = topTypeEquivalentToSuppliedType
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
        val topTypeSig = typeMap[typeSig]?.let {
            topTypeMap[it]
        }
        val rt = topTypeSig?.let {
            // the top does not always have the concrete type
            top.getDataFor(it)
        }
        return rt
    }
}


data class Q1<K, V>(val l: Map<K, V>)
data class Q2<T>(val l: List<T>)
data class Q3<T>(val q2: Q2<T>, val l2: List<T>)
data class Q4<T>(val q3: Q3<T>)
data class A(val d: Double, val str: String)
data class Q5<E>(val q1: Q1<Int, E>)


data class Inner0<I0_1,I0_2>(
    val t1:I0_1,
    val t2:I0_2
)
data class Inner1<I1_1, I1_2, I1_3>(
    val inner0: Inner0<I1_2,I1_3>,
)
data class Q6<Q6_1, Q6_2>(
    val l: Inner1<Q6_1, Double, Q6_2>
)




/**
 * A param type map, is a type map extracted from enclusure class, but the index is relevant to the param.
 */
fun makeParamTypeMap(
    constructorParam: KParameter,
    enclosureRDClassData: RDClassData,
): Map<Int, RDClassData> {

    val ktype = constructorParam.type
    val arguments = ktype.arguments
    /**
     * Perform lookup on [enclosureRDClassData] to know which concrete types are passed to this [constructorParam] in place of its generic type, at which index
     */
    val typeMapFromEnclosure: Map<Int, RDClassData> = arguments.withIndex().mapNotNull { (index, arg) ->
        // only consider type parameter, ignore the rest
        val argTypeParam = arg.type?.classifier as? KTypeParameter
        val concreteType = argTypeParam?.let { enclosureRDClassData.getDataFor(it) }
        val pair = concreteType?.let {
            index to it
        }
        pair
    }.toMap()
    return typeMapFromEnclosure
}

fun main() {

    val q6 = RDClassData.from<Q6<Int, String>>()
    val q6ProvideMap = q6.directProvideMap2

    q6.kClass.primaryConstructor!!.parameters.forEach { inner1Param ->

        /**
         * Will this work?
         * => This will work because:
         * Each parameter can use the information from its enclosing class (enclosure) to construct a full map (with index) of generic - concrete type that it can use to query later.
         * Whatever parameter cannot get from enclosure, it can get from within itself.
         *
         * This process can be repeated for deeper parameter, each only need to construct 1 map from its enclosure's data.
         * Remember, each mapping must only the information from the immediate enclosure.
         */

        val typeMapForInner1 = makeParamTypeMap(inner1Param,q6)
        val inner1Class = inner1Param.type.classifier as KClass<*>
        val inner1RD = RDClassData(inner1Class,inner1Param.type)
        val inner1TypeMap:Map<String,RDClassData> = inner1RD.makeConjunctionProvideMap2(q6ProvideMap)

        inner1Class.primaryConstructor!!.parameters.map { inner0 ->

            val inner0Class = inner0.type.classifier as KClass<*>
            val inner0RD = RDClassData(inner0Class, inner0.type)
            val inner0FullProvideMap = inner0RD.makeConjunctionProvideMap2(inner1TypeMap)
            val index = inner0.index
            val inner0Classifier = inner0.type.classifier

            when (inner0Classifier) {
                is KClass<*> -> {
                    inner0Classifier.primaryConstructor!!.parameters.map { paramOfInner0->
                        val paramOfInner0 = paramOfInner0.type.classifier
                        when(paramOfInner0){
                            is KTypeParameter ->{
                                val rdDataFromInner1 = inner0FullProvideMap[paramOfInner0.name]
                                println("+++++ rdDataFromInner1: ${paramOfInner0.name} :${rdDataFromInner1}")
                            }
                        }
                    }
                    println("")
                }
                is KTypeParameter -> {
                    // lookup type from the outer type map
                    val outerType = typeMapForInner1[index]
                    if (outerType != null) {
                        println("outer: ${outerType}")
                    } else {
                        // lookup type from within the parameter
                        val type = inner1Param.type.arguments[index].type!!
                        val c = type.classifier as KClass<*>
                        val rd = RDClassData(c,type)
                        println("inside: ${rd}")
                    }
                }
            }
        }
    }
}
