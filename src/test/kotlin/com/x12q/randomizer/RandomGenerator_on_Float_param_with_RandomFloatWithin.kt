package com.x12q.randomizer

import com.x12q.randomizer.annotations.number._float.RandomFloatWithin
import com.x12q.randomizer.annotations.number._int.RandomIntWithin
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.floats.shouldBeBetween
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_Float_param_with_RandomFloatWithin {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomFloatWithin(1f,2f)
        val i:Float
    )

    @Test
    fun `on float param`() {
        (rdm.random(RDClassData.from<B>()) as B).i.shouldBeBetween(1f,2f,0.00001f)
    }

    data class B2(
        @RandomFloatWithin(1f,2f)
        val f:Double
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomFloatWithin(1f,2f)
        val i:T
    )

    @Test
    fun `on float generic type param`() {
        (rdm.random(RDClassData.from<B3<Float>>()) as B3<Float>).i.shouldBeBetween(1f,2f,0.000001f)
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B3<String>>())
        }
    }

}
