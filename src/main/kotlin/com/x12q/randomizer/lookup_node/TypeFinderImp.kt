package com.x12q.randomizer.lookup_node

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.primaryConstructor


data class Q1<K, V>(val l: Map<K, V>)
data class Q2<T>(val l: List<T>)
data class Q3<T>(val q2: Q2<T>, val l2: List<T>)
data class Q4<T>(val q3: Q3<T>)
data class A(val d: Double, val str: String)
data class Q5<E>(val q1: Q1<Int, E>)


data class Inner0<I0_1, I0_2>(
    val t1: I0_1,
    val t2: I0_2
)

data class Inner1<I1_1, I1_2, I1_3>(
    val inner0: Inner0<I1_2, I1_3>,
)

data class Q6<Q6_1, Q6_2>(
    val l: Inner1<Q6_1, Double, Q6_2>
)

fun main() {

    val q6 = RDClassData.from<Q6<Int, String>>()
    val q6ProvideMap = q6.makeConjunctionProvideMap2(emptyMap())

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

        val inner1Class = inner1Param.type.classifier as KClass<*>
        val inner1RD = RDClassData(inner1Class, inner1Param.type)
        val inner1TypeMap: Map<String, RDClassData> = inner1RD.makeConjunctionProvideMap2(q6ProvideMap)

        inner1Class.primaryConstructor!!.parameters.map { inner0 ->

            val inner0Class = inner0.type.classifier as KClass<*>
            val inner0RD = RDClassData(inner0Class, inner0.type)
            val inner0FullProvideMap = inner0RD.makeConjunctionProvideMap2(inner1TypeMap)
            val index = inner0.index
            val inner0Classifier = inner0.type.classifier

            when (inner0Classifier) {
                is KClass<*> -> {
                    inner0Classifier.primaryConstructor!!.parameters.map { paramOfInner0 ->
                        val paramOfInner0 = paramOfInner0.type.classifier
                        when (paramOfInner0) {
                            is KTypeParameter -> {
                                val rdDataFromInner1 = inner0FullProvideMap[paramOfInner0.name]
                                println("+++++ rdDataFromInner1: ${paramOfInner0.name} :${rdDataFromInner1}")
                            }
                        }
                    }
                    println("")
                }

                is KTypeParameter -> {
                    // lookup type from the outer type map
                    // lookup type from within the parameter
                    val type = inner1Param.type.arguments[index].type!!
                    val c = type.classifier as KClass<*>
                    val rd = RDClassData(c, type)
                    println("inside: ${rd}")
                }
            }
        }
    }
}
