package com.x12q.randomizer.lookup_node

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IndirectLookupNodeTest{

    class JKL<E>(val e:E)
    // E is mapped to T
    class TWQ<D,T,X>(val t:JKL<T>, val d:D, val x:X)

    class MMM<Y,U,H,C>(val twq:TWQ<Y,U,H>, val c:C)

    @Test
    fun getDataFor(){

        val rd1 = RDClassData.from<JKL<Double>>()
        val rd2 = RDClassData.from<TWQ<Double,String,Boolean>>()
        val rd3 =RDClassData.from<MMM<Double,Short,Float,Int>>()

        val node1 = IndirectLookupNode(
            frontParam = rd1.kClass.typeParameters[0], //E
            backParam = rd2.kClass.typeParameters[1], //T
            backNode = rd2 //T->String
        )
        node1.getDataFor(rd1.kClass.typeParameters[0]) shouldBe RDClassData.from<String>()


        val node3_0 = IndirectLookupNode(
            frontParam = rd2.kClass.typeParameters[1], //T
            backParam = rd3.kClass.typeParameters[1], //U
            backNode = rd3 //U->Short
        )

        val node3 = IndirectLookupNode(
            frontParam = rd1.kClass.typeParameters[0], //E
            backParam = rd2.kClass.typeParameters[1], //X
            backNode = node3_0 //X->T in node3_0
        )
        node3.getDataFor(rd1.kClass.typeParameters[0]) shouldBe RDClassData.from<Short>()

    }
}
