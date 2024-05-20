package com.x12q.randomizer

import com.x12q.randomizer.RandomGenerator_on_Double_param_with_RandomDoubleOneOf.B3
import com.x12q.randomizer.annotations.number._double.RandomDoubleWithin
import com.x12q.randomizer.annotations.number._float.RandomFloatWithin
import com.x12q.randomizer.annotations.number._int.RandomIntWithin
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.floats.shouldBeBetween
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_Double_param_with_RandomDoubleWithin {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomDoubleWithin(1.0,2.0)
        val i:Double
    )

    @Test
    fun `on Double param`() {
        (rdm.random(RDClassData.from<B>()) as B).i.shouldBeBetween(1.0,2.0,0.00001)
    }

    data class B2(
        @RandomDoubleWithin(1.0,2.0)
        val f:String
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomDoubleWithin(1.0,2.0)
        val i:T
    )

    @Test
    fun `on Double generic type param`() {
        (rdm.random(RDClassData.from<B3<Double>>()) as B3<Double>).i.shouldBeBetween(1.0,2.0,0.000001)
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B3<String>>())
        }
    }

}
