package com.x12q.randomizer.lookup_node

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RDClassDataTest{

    class TWQ<T>(val t: T)

    @Test
    fun getDataFor(){
        val rd = RDClassData.from<TWQ<Int>>()

        val typeParam = rd.kClass.typeParameters[0]

        rd.getDataFor(typeParam) shouldBe RDClassData.from<Int>()
    }

    @Test
    fun getClassFor(){
        val rd = RDClassData.from<TWQ<Int>>()
        val typeParam = rd.kClass.typeParameters[0]
        rd.getKClassFor(typeParam) shouldBe Int::class
    }
}
