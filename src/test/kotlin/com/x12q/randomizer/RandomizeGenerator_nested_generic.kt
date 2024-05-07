package com.x12q.randomizer

import com.x12q.randomizer.RandomizeGenerator_nested_generic.*
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.builder.randomizers
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldNotThrow
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
    data class Q3<T>(val q2:Q2<T>, val l2:List<T>)
    data class Q4<T>(val q3:Q3<T>)
    data class A(val d:Double,val str:String)
    data class Q5<E>(val q1:Q1<Int,E>)


    data class A1<T,Q>(val t:T,val q:Q)
    data class A2<E>(val a1:A1<E,Double>)

    @Test
    fun case1(){
//        shouldNotThrow<Throwable> {
//            println(rdm.random(RDClassData.from<Q3<Int>>()))
//            println(rdm.random(RDClassData.from<Q4<A>>()))
//            println(rdm.random(RDClassData.from<Q4<Q2<Int>>>()))

//            println(rdm.random(RDClassData.from<Q5<Int>>()))
//        }
    }

    @Test
    fun case2(){
        println(rdm.random(RDClassData.from<Q4<Q3<Q2<Q4<Int>>>>>()))

    }

    @Test
    fun case3(){
        println(rdm.random(RDClassData.from<Q4<Int>>()))
    }

//    @Test
    fun forever(){
        println(rdm.random(RDClassData.from<Q4<Q3<Q2<Q2<Q4<Q3<Q2<Q2<Int>>>>>>>>>()))
    }

}
