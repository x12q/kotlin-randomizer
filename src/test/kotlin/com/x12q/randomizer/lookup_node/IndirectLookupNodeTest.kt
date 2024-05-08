package com.x12q.randomizer.lookup_node

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IndirectLookupNodeTest{

    class JKL<E>(val e:E)
    // E is mapped to T
    class TWQ<D,T,X>(val t:JKL<T>, val d:D, val x:X)

    @Test
    fun getDataFor(){

        val rd1 = RDClassData.from<JKL<Double>>()
        val rd2 = RDClassData.from<TWQ<Double,String,Boolean>>()

        val node = IndirectLookupNode(
            frontParam = rd1.kClass.typeParameters[0],
            backParam = rd2.kClass.typeParameters[1],
            backNode = rd2
        )
        node.getDataFor(rd1.kClass.typeParameters[0]) shouldBe RDClassData.from<String>()
    }
}
