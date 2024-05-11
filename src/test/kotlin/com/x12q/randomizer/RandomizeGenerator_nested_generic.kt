package com.x12q.randomizer

import com.x12q.randomizer.RandomizeGenerator_nested_generic.*
import com.x12q.randomizer.lookup_node.RDClassData
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldNotThrow
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.test.BeforeTest
import kotlin.test.Test



class RandomizeGenerator_nested_generic {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class Q1<K,V>(val l:Map<K,V>)
    data class Q2<T>(val l:List<T>)
    data class Q2_2<X1,X2>(val l:Map<X2,X1>)
    data class Q3<T>(val q2:Q2<T>)
    data class Q4<T>(val q3:Q3<T>)
    data class A(val d:Double,val str:String)
    data class Q5<E>(val q1:Q1<Int,E>)


    data class A1<T,Q>(val t:T,val q:Q)
    data class A2<E>(val a1:A1<E,Double>)

    data class RD1<T1>(val e: T1)

    data class RD2<T2_1, T2_2, T2_3>(
        val t: RD1<T2_2>,
        val d: T2_1,
        val x: T2_3,
    )

    data class RD3<T3_1, T3_2, T3_3, T3_4>(
        val rd2: RD2<T3_1, T3_2, T3_4>,
        val c: T3_3,
    )
    @Test
    fun case3(){
        println(rdm.random(RDClassData.from<Q4<Int>>()))
//        println(rdm.random(RDClassData.from<Q4<Q3<Int>>>()))
    }

    @Test
    fun rrr(){
        println(rdm.random(RDClassData.from<RD2<Int,Double,Float>>()))
        println(rdm.random(RDClassData.from<RD3<Int,Boolean,Float,String>>()))
    }


    @Test
    fun case1(){
        shouldNotThrow<Throwable> {
            println(rdm.random(RDClassData.from<Q3<Int>>()))
            println(rdm.random(RDClassData.from<Q4<A>>()))
            println(rdm.random(RDClassData.from<Q4<Q2<Int>>>()))
            println(rdm.random(RDClassData.from<Q5<Double>>()))
            println(rdm.random(RDClassData.from<Q2_2<Int,String>>()))
            println(rdm.random(RDClassData.from<Q2<Int>>()))
            println(rdm.random(RDClassData.from<A2<Int>>()))
        }
    }

    @Test
    fun case2(){
        println(rdm.random(RDClassData.from<Q4<Q3<Q2<Q4<Int>>>>>()))
    }

//    @Test
    fun forever(){
        println(rdm.random(RDClassData.from<Q4<Q3<Q2<Q2<Q4<Q3<Q2<Q2<Int>>>>>>>>>()))
    }

}


fun main(){
    val rd = RDClassData.from<Q5<Double>>()
    println("====Q5 type param in class ====")
    println("${rd.kClass.simpleName} ${rd.kClass.typeParameters}")
    println(rd.kType)
    println("====Q5 type param in constructor ====")
    val q5Con = rd.kClass.primaryConstructor!!
    println(q5Con.typeParameters)
    println("====Q1 type param in class ====")

    val q1Param = q5Con.parameters[0]
    val q1KType = q1Param.type
    val q1Classifier = q1KType.classifier
    val q1Class = q1Classifier as KClass<*>
    println("${q1Class.simpleName} ${q1Class.typeParameters}")
    println("${q1Class.simpleName} ${q1KType.arguments}")


    val q1Constructor = q1Class.primaryConstructor!!
    val mapParam = q1Constructor.parameters[0]
    println("====Map<K,V>====")

    val mapKType = mapParam.type
    val mapClass = mapKType.classifier as KClass<*>

    println("Map ${mapClass.typeParameters}")
    println("Map ${mapKType.arguments}")


}
