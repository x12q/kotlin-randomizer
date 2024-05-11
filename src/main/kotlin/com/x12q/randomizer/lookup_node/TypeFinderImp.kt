package com.x12q.randomizer.lookup_node

import com.x12q.randomizer.util.ReflectionUtils.makeTypeMap
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


data class Inner1<I1, I2, I3>(val i1: I1, val i2: I2, val i3: I2)
data class Q6<Q6_1, Q6_2>(
    val l: Inner1<Q6_1, Double, Q6_2>
)

///**
// * Construct an index-type map for a particular [constructorParam], using type data from [parentRDClassData].
// * The index of the output map is: the index of generic type appear in [constructorParam]
// * Example:
// * For parameter of type Q<T1,T2,T3>, and given that T1 -> String, T3->Double, T3 -> another generic from enclosure
// */
//fun makeTypeMap(
//    constructorParam: KParameter,
//    parentRDClassData: RDClassData,
//): Map<Int, RDClassData> {
//    /**
//     * This gives the entire type structure of the param
//     * Eg: Q1<Q2<Int>>
//     */
//    val ktype = constructorParam.type
//
//    /**
//     * Perform lookup on [parentRDClassData] to know which concrete types are passed to this [constructorParam] in place of its generic type, at which index
//     */
//    val typeMapFromEnclosure: Map<Int, RDClassData> = ktype.arguments.withIndex().mapNotNull { (index, e) ->
//        // only consider type parameter, ignore the rest
//        val ktypeParam = e.type?.classifier as? KTypeParameter
//        val concreteType = ktypeParam?.let { parentRDClassData.getDataFor(it) }
//        val pair = concreteType?.let {
//            index to it
//        }
//        pair
//    }.toMap()
//    return typeMapFromEnclosure
//}

fun main() {
    val q6 = RDClassData.from<Q6<Int, String>>()

    q6.kClass.primaryConstructor!!.parameters.forEach { parameter ->

        /**
         * Will this work?
         * => This will work because:
         * Each parameter can use the information from its enclosing class (enclosure) to construct a full map (with index) of generic - concrete type that it can use to query later.
         * Whatever parameter cannot get from enclosure, it can get from within itself.
         *
         * This process can be repeated for deeper parameter, each only need to construct 1 map from its enclosure's data.
         * Remember, each mapping must only the information from the immediate enclosure.
         */
        println(parameter.type)

        val typeMapFromEnclosure = makeTypeMap(parameter,q6)

        val paramClass = parameter.type.classifier as KClass<*>

        // here it can perform lookup using the map above + data within itself to construct a full list of generic -> concrete mapping
        paramClass.primaryConstructor!!.parameters.withIndex().map { (index, innerParam) ->
            val classifier = innerParam.type.classifier
            when (classifier) {
                is KClass<*> -> println("class: ${classifier}")
                is KTypeParameter -> {
                    // lookup type from the outer type map
                    val outerType = typeMapFromEnclosure[index]
                    if (outerType != null) {
                        println("outer: ${outerType}")
                    } else {
                        // lookup type from within the parameter
                        val type = parameter.type.arguments[index].type!!
                        val c = type.classifier as KClass<*>
                        val rd = RDClassData(c,type)
                        println("inside: ${rd}")
                    }
                }
            }
        }
    }
}
