package com.x12q.randomizer

import com.x12q.randomizer.RandomGenerator_on_Double_param_with_RandomDoubleFixed.B3
import com.x12q.randomizer.annotations.number._double.RandomDoubleOneOf
import com.x12q.randomizer.annotations.number._float.RandomFloatOneOf
import com.x12q.randomizer.annotations.number._int.RandomIntOneOf
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_Double_param_with_RandomDoubleOneOf {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomDoubleOneOf([100.0])
        val i:Double
    )

    @Test
    fun `on Double param`() {
        (rdm.random(RDClassData.from<B>()) as B).i shouldBe 100f
    }

    data class B2(
        @RandomDoubleOneOf([33.0])
        val f:Int
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomDoubleOneOf([123.0])
        val i:T
    )

    @Test
    fun `on Double generic type param`() {
        (rdm.random(RDClassData.from<B3<Double>>()) as B3<Double>).i shouldBe 123f
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B3<String>>())
        }
    }


}
