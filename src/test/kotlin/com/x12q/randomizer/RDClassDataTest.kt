package com.x12q.randomizer

import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test

class RDClassDataTest {


    class Q1<Q1_1, Q1_2>(
        val t: Q1_1
    )
    class Q2<Q2_1, Q2_2>(
        val t: Q2_1
    )

    class Q0<Q0_1, Q0_2>(
        val q1: Q1<Q0_2, Double>,
        val q2: Q2<Q0_2, Q0_2>
    )


    @Test
    fun makeIndirectProvideMap() {
        val q0 = RDClassData.from<Q0<Int, String>>()

        /**
         * on Q1
         */

        val q1Param = q0.kClass.primaryConstructor!!.parameters[0]

        val q1 = RDClassData(
            q1Param.type.classifier as KClass<*>,
            q1Param.type
        )

        q1.directDeclaredTypeMap shouldBe mapOf(
            "Q1_2" to RDClassData.from<Double>()
        )

        q1.makeIndirectTypeMap(q0.directDeclaredTypeMap) shouldContainExactly mapOf(
            "Q1_1" to RDClassData.from<String>()
        )

        q1.makeCompositeDeclaredTypeMap(q0.directDeclaredTypeMap) shouldContainExactly mapOf(
            "Q1_1" to RDClassData.from<String>(),
            "Q1_2" to RDClassData.from<Double>()
        )

        /**
         * on Q2
         */

        val q2Param = q0.kClass.primaryConstructor!!.parameters[1]
        val q2 = RDClassData(
            q2Param.type.classifier as KClass<*>,
            q2Param.type
        )

        q2.directDeclaredTypeMap shouldBe emptyMap()
        q2.makeIndirectTypeMap(q0.directDeclaredTypeMap) shouldContainExactly mapOf(
            "Q2_1" to RDClassData.from<String>(),
            "Q2_2" to RDClassData.from<String>(),
        )

    }

    @Test
    fun directProvideMap() {
        val rd = RDClassData.from<Q0<Int, String>>()
        rd.directDeclaredTypeMap shouldContainAll mapOf(
            "Q0_1" to RDClassData.from<Int>(),
            "Q0_2" to RDClassData.from<String>()
        )
    }

    class TWQ<T>(val t: T)

    @Test
    fun getDataFor() {
        val rd = RDClassData.from<TWQ<Int>>()
        val typeParam = rd.kClass.typeParameters[0]
        rd.getDataFor(typeParam) shouldBe RDClassData.from<Int>()
    }

    @Test
    fun getClassFor() {
        val rd = RDClassData.from<TWQ<Int>>()
        val typeParam = rd.kClass.typeParameters[0]
        rd.getKClassFor(typeParam) shouldBe Int::class
    }
}
